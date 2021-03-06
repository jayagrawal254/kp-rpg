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
 * Segnungs die -- a die with symbols used by HeXXen 1733.
 *
 * The following table is used for lookups:
 *
 * 1,2 = E (Esprit), 4,5 = H (Hex), 6 = HH (double Hex)
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0 2021-01-06
 */
@Dependent
public class Segn extends D6 {
    @Override
    public DieResult roll() {
        int roll = rollSingle();

        String result = " ";
        switch (roll) {
            case 1:
            case 2:
                result = "E";
                break;
            case 4:
            case 5:
                result = "H";
                break;
            case 6:
                result = "HH";
                break;
        }

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
