/*
 * Copyright (C) 2011 - 2015, MyWarp team and contributors
 *
 * This file is part of MyWarp.
 *
 * MyWarp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyWarp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyWarp. If not, see <http://www.gnu.org/licenses/>.
 */

package me.taylorkelly.mywarp.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.storage.generated.Tables;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;

import javax.sql.DataSource;

/**
 * Creates {@link WarpStorage} instances.
 */
public class WarpStorageFactory {

  private static final ImmutableSet<SQLDialect>
      SUPPORTED_DIALECTS =
      ImmutableSet.of(SQLDialect.MYSQL, SQLDialect.MARIADB, SQLDialect.SQLITE, SQLDialect.H2);

  /**
   * Block initialization of this class.
   */
  private WarpStorageFactory() {
  }

  /**
   * Creates a new {@code WarpStorage} to the given {@code DataSource}.
   * <p>Use {@link #createInitialized(MyWarp, DataSource, ConnectionConfiguration)} to create an initialized {@code
   * WarpStorage} that guarantees existence of MyWarp's table structure.</p>
   *
   * @param myWarp     the MyWarp instance
   * @param dataSource the DataSource
   * @param config     the config
   * @return the {@code WarpStorage}
   * @throws StorageInitializationException if a database error occurs or the underling database management system is
   *                                        not supported
   */
  public static WarpStorage create(MyWarp myWarp, DataSource dataSource, ConnectionConfiguration config)
      throws StorageInitializationException {
    SQLDialect dialect = config.getDialect();
    if (!SUPPORTED_DIALECTS.contains(dialect)) {
      throw new StorageInitializationException(String.format("%s is not supported!", dialect.getName()));
    }
    return createRelationalWarpStorage(myWarp, config.getDialect(), createSettings(config), dataSource);
  }

  /**
   * Creates a new initialized {@code WarpStorage} to the given {@code DataSource}, attempting to create or update
   * MyWarp's table structure if necessary.
   * <p>Use {@link #create(MyWarp, DataSource, ConnectionConfiguration)} to create a {@code WarpStorage} that does
   * not create or update the table structure.</p>
   *
   * @param myWarp     the MyWarp instance
   * @param dataSource the DataSource
   * @param config     the config
   * @return the {@code WarpStorage}
   * @throws StorageInitializationException if a database error occurs, the underling database management system is
   *                                        not supported or initialization of MyWarp's table structure fails
   */
  public static WarpStorage createInitialized(MyWarp myWarp, DataSource dataSource, ConnectionConfiguration config)
      throws StorageInitializationException {
    SQLDialect dialect = config.getDialect();
    if (!SUPPORTED_DIALECTS.contains(dialect)) {
      throw new StorageInitializationException(String.format("%s is not supported!", dialect.getName()));
    }

    Flyway flyway = new Flyway();
    flyway.setClassLoader(myWarp.getClass().getClassLoader());
    flyway.setDataSource(dataSource);
    flyway.setLocations(getMigrationLocation(dialect));

    if (config.supportsSchemas()) {
      flyway.setSchemas(config.getSchema());
      flyway.setPlaceholders(ImmutableMap.of("schema", config.getSchema()));
    }

    try {
      //Fix stored checksums on databases that where created with older scripts
      flyway.repair();
      flyway.migrate();
    } catch (FlywayException e) {
      throw new StorageInitializationException("Failed to execute migration process.", e);
    }

    return createRelationalWarpStorage(myWarp, dialect, createSettings(config), dataSource);
  }

  /**
   * Creates a new {@code RelationalWarpStorage} using the given parameters.
   *
   * @param myWarp     the MyWarp instance
   * @param dataSource the DataSource
   * @return a new {@code RelationalWarpStorage}
   */
  private static RelationalWarpStorage createRelationalWarpStorage(MyWarp myWarp, SQLDialect dialect, Settings settings,
                                                                   DataSource dataSource) {
    return new RelationalWarpStorage(myWarp, new DefaultConfiguration().set(dialect).set(settings).set(dataSource));
  }

  /**
   * Creates a new {@link Settings} instance based on the given {@code config}.
   *
   * @param config the config
   * @return a new {@code Settings} instance
   */
  private static Settings createSettings(ConnectionConfiguration config) {
    Settings settings = new Settings();
    if (config.supportsSchemas()) {
      settings.withRenderMapping(new RenderMapping().withSchemata(
          new MappedSchema().withInput(Tables.WARP.getSchema().getName()).withOutput(config.getSchema())));
    } else {
      settings.withRenderSchema(false);
    }
    return settings;
  }

  /**
   * Gets the location to scan for migration scripts for the database management systems of the given {@code dialect}.
   *
   * @param dialect the dialect
   * @throws StorageInitializationException if no migration scripts for the given {@code dialect} are bundled
   * @see Flyway#setLocations(String...)
   */
  private static String getMigrationLocation(SQLDialect dialect) throws StorageInitializationException {
    switch (dialect) {
      case H2:
        return "classpath:migrations/h2";
      case MARIADB:
      case MYSQL:
        return "classpath:migrations/mysql";
      case SQLITE:
        return "classpath:migrations/sqlite";
      default:
        throw new StorageInitializationException(
            String.format("Migrations are not supported for %s!", dialect.getName()));
    }
  }

}
