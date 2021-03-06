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

package me.taylorkelly.mywarp.limits;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import me.taylorkelly.mywarp.LocalPlayer;
import me.taylorkelly.mywarp.LocalWorld;
import me.taylorkelly.mywarp.util.IterableUtils;
import me.taylorkelly.mywarp.util.WarpUtils;
import me.taylorkelly.mywarp.warp.Warp;
import me.taylorkelly.mywarp.warp.WarpManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages {@link Limit}s. <p> The SimpleLimitManager operates on a {@link LimitProvider} that provides the actual
 * limits and a {@link WarpManager} that holds the warps the limits apply on. </p>
 */
public class SimpleLimitManager implements LimitManager {

  private final LimitProvider provider;
  private final WarpManager manager;

  /**
   * Initializes this SimpleLimitManager acting on the given LimitProvider and the given WarpManager.
   *
   * @param provider the LimitProvider
   * @param manager  the WarpManager
   */
  public SimpleLimitManager(LimitProvider provider, WarpManager manager) {
    this.provider = provider;
    this.manager = manager;
  }

  @Override
  public LimitManager.EvaluationResult evaluateLimit(LocalPlayer creator, LocalWorld world, Limit.Type type,
                                                     boolean evaluateParents) {
    if (!type.canDisobey(creator, world)) {

      Iterable<Warp> filteredWarps = manager.filter(WarpUtils.isCreator(creator.getProfile()));
      Limit limit = provider.getLimit(creator, world);

      List<Limit.Type> limitsToCheck = Lists.newArrayList(type);
      if (evaluateParents) {
        limitsToCheck.addAll(type.getParentsRecusive());
      }

      for (Limit.Type check : limitsToCheck) {
        LimitManager.EvaluationResult result = evaluateLimit(limit, check, filteredWarps);
        if (result.exceedsLimit()) {
          return result;
        }
      }
    }
    return LimitManager.EvaluationResult.LIMIT_MEAT;
  }

  /**
   * Evaluates whether the given Limit.Type of the given Limit is exceeded in the given Iterable of warps. The Iterable
   * will be overwritten and only include the warps matching the given types condition.
   *
   * @param limit         the limit
   * @param type          the type
   * @param filteredWarps the warps
   * @return an EvaluationResult representing the result of the evaluation
   */
  private LimitManager.EvaluationResult evaluateLimit(Limit limit, Limit.Type type, Iterable<Warp> filteredWarps) {
    filteredWarps = Iterables.filter(filteredWarps, type.getCondition());
    int limitMaximum = limit.getLimit(type);
    if (IterableUtils.atLeast(filteredWarps, limitMaximum)) {
      return new LimitManager.EvaluationResult(type, limitMaximum);
    }
    return LimitManager.EvaluationResult.LIMIT_MEAT;
  }

  @Override
  public Map<Limit, List<Warp>> getWarpsPerLimit(LocalPlayer creator) {
    Collection<Warp> warps = manager.filter(WarpUtils.isCreator(creator.getProfile()));
    Map<Limit, List<Warp>> ret = new HashMap<Limit, List<Warp>>();

    for (Limit limit : provider.getEffectiveLimits(creator)) {
      ret.put(limit, new ArrayList<Warp>());
    }

    // sort warps to limits
    for (Warp warp : warps) {
      for (Limit limit : ret.keySet()) {
        if (limit.isAffectedWorld(warp.getWorld())) {
         ret.get(limit).add(warp);
        }
      }
    }
    return ret;
  }

}
