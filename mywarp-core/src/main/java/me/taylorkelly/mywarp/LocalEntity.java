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

package me.taylorkelly.mywarp;

import me.taylorkelly.mywarp.util.EulerDirection;
import me.taylorkelly.mywarp.util.Vector3;

/**
 * Represents an entity.
 */
public interface LocalEntity {

  /**
   * Gets the world this entity is currently positioned in.
   *
   * @return the current world
   */
  LocalWorld getWorld();

  /**
   * Gets the current position of this entity.
   *
   * @return the current position
   */
  Vector3 getPosition();

  /**
   * Gets the current rotation of this entity.
   *
   * @return the current rotation
   */
  EulerDirection getRotation();

  /**
   * Teleports this entity to the given position on the given world, and sets his rotation to the given one.
   *
   * @param world    the world
   * @param position the position vector
   * @param rotation the rotation
   */
  void teleport(LocalWorld world, Vector3 position, EulerDirection rotation);

}
