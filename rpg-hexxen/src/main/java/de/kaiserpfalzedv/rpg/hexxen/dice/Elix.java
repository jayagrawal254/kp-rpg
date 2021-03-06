/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.rpg.hexxen.dice;

import de.kaiserpfalzedv.rpg.core.dice.bag.D6;
import de.kaiserpfalzedv.rpg.core.dice.mat.DieResult;
import de.kaiserpfalzedv.rpg.core.dice.mat.ImmutableDieResult;

import javax.enterprise.context.Dependent;

/**
 * Exlixir die -- a die with symbols used by HeXXen 1733.
 *
 * The following table is used for lookups:
 *
 * 1-5 = 1-5, 6=3
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0 2021-01-06
 */
@Dependent
public class Elix extends D6 {
    @Override
    public DieResult roll() {
        int roll = rollSingle();

        String result = (roll == 6) ? "3" : Integer.toString(roll, 10);

        return ImmutableDieResult.builder()
                .die(this)
                .total(result)
                .rolls(result)
                .build();
    }

    @Override
    public boolean isNumericDie() {
        return false;
    }
}
