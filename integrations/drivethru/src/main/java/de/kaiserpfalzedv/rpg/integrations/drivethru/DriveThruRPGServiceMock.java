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

package de.kaiserpfalzedv.rpg.integrations.drivethru;

import de.kaiserpfalzedv.rpg.core.dice.bag.D100;
import de.kaiserpfalzedv.rpg.core.user.InvalidUserException;
import de.kaiserpfalzedv.rpg.core.user.User;
import de.kaiserpfalzedv.rpg.integrations.drivethru.model.*;
import de.kaiserpfalzedv.rpg.integrations.drivethru.resource.NoDriveThruRPGAPIKeyDefinedException;
import de.kaiserpfalzedv.rpg.integrations.drivethru.resource.NoValidTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

import static java.time.ZoneOffset.UTC;


/**
 * DriveThruRPGServiceMock -- a mocked service for using in unit tests.
 * <p>
 * The following cases are defined for tests. They depend on the user name of the specified user:
 *
 * <dl>
 *     <dt>invalid#0001</dt><dd>Throws an {@link InvalidUserException}</dd>
 *     <dt>invalid#0002</dt><dd>Throws an {@link NoDriveThruRPGAPIKeyDefinedException}</dd>
 *     <dt>invalid#0003</dt><dd>Returns an expired token</dd>
 *     <dt>invalid#0004</dt><dd>Throws an {@link NoValidTokenException}</dd>
 *     <dt><em>any other name</em></dt><dd>Return a token valid for the next hour</dd>
 * </dl>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.2.0  2021-02-06
 */
public class DriveThruRPGServiceMock implements DriveThruRPGService {
    private static final Logger LOG = LoggerFactory.getLogger(DriveThruRPGServiceMock.class);

    /**
     * Mocks the token service.
     *
     * @param user The user to authorize to DriveThruRPG.
     * @return a token.
     * @throws InvalidUserException                 for username {@literal invalid#0001}
     * @throws NoDriveThruRPGAPIKeyDefinedException for username {@literal invalid#0002}
     * @throws NoValidTokenException                for username {@literal invalid#0004}
     */
    @Override
    public Token getToken(final User user) throws InvalidUserException, NoDriveThruRPGAPIKeyDefinedException, NoValidTokenException {
        switch (user.getName().toLowerCase(Locale.ROOT)) {
            case "invalid#0001":
                LOG.debug("Throwing InvalidUserException. user='{}'", user.getDisplayName());
                throw new InvalidUserException(user);

            case "invalid#0002":
                LOG.debug("Throwing NoDriveThruRPGAPIKeyDefinedException. user='{}'", user.getDisplayName());
                throw new NoDriveThruRPGAPIKeyDefinedException(user);

            case "invalid#0004":
                LOG.debug("Throwing NoValidTokenException. user='{}'", user.getDisplayName());
                throw new NoValidTokenException(user);
        }

        LocalDateTime now = LocalDateTime.now(UTC);

        Token token = ImmutableToken.builder()
                .accessToken(UUID.randomUUID().toString())
                .customerId(user.getName().split("#")[1])

                .expireTime(LocalDateTime.MAX)
                .serverTime(now)
                .localTime(now)
                .expires(LocalDateTime.MAX.toEpochSecond(UTC) - now.toEpochSecond(UTC))

                .build();

        if ("invalid#0003".equalsIgnoreCase(user.getName())) {
            token = ImmutableToken.builder()
                    .from(token)

                    .expireTime(now)
                    .serverTime(now.minusHours(1))
                    .localTime(now.minusHours(1))
                    .expires(3600L)

                    .build();
        }

        LOG.trace("Token created. user='{}', token={}", user.getDisplayName(), token);
        return token;
    }

    /**
     * getProduct -- mocks the product.
     *
     * @param productId The product id to load the data for. If the product id is a negative integer, an empty optional
     *                  will be returned.
     * @return The product dataset.
     */
    @Override
    public Optional<Product> getProduct(final String productId) {
        if (Integer.parseInt(productId, 10) < 0) {
            LOG.trace("Product not found. productId={}", productId);
            return Optional.empty();
        }

        LOG.trace("Product created. productId={}", productId);
        String productUrl = "https://nowhere/product/" + productId;
        return Optional.of(
                ImmutableProduct.builder()
                        .productsId(productId)
                        .productsName("Product Nr. 1")

                        .publisherId("1")
                        .publisherName("Publisher Nr. 1")

                        .coverURL(productUrl + "/cover")
                        .thumbnail(productUrl + "/thumb")
                        .thumbnail40(productUrl + "/thumb40")
                        .thumbnail80(productUrl + "/thumb80")
                        .thumbnail100(productUrl + "/thumb100")

                        .build()
        );
    }

    /**
     * getPublisher -- mocks the publisher service.
     *
     * @param publisherId The pubisher id to load the data for. If the publisherId is a negative integer, an empty
     *                    optional will be returned.
     * @return the publisher dataset.
     */
    @Override
    public Optional<Publisher> getPublisher(final String publisherId) {
        if (Integer.parseInt(publisherId, 10) < 0) {
            LOG.trace("No publisher found. publisherId={}", publisherId);
            return Optional.empty();
        }

        LOG.trace("Publisher created. publisherId={}", publisherId);
        return Optional.of(
                ImmutablePublisher.builder()
                        .publisherId(publisherId)
                        .publisherName("Publisher Nr. " + publisherId)
                        .build()
        );
    }

    /**
     * getOwnedProducts -- mocks the owned product.
     * <p>
     * A token will be requested, so the behavior described in {@link #getToken(User)} will also be mimicked by this
     * method.
     * <p>
     * In Addition, the name {@literal valid#0001} will return an empty list. Every other name will return a D100
     * entries.
     *
     * @param user The user to load the data as.
     * @return The owned products.
     * @throws InvalidUserException                 for username {@literal invalid#0001}
     * @throws NoDriveThruRPGAPIKeyDefinedException for username {@literal invalid#0002}
     * @throws NoValidTokenException                for username {@literal invalid#0004}
     */
    @Override
    public List<OwnedProduct> getOwnedProducts(final User user) throws NoValidTokenException, NoDriveThruRPGAPIKeyDefinedException, InvalidUserException {
        getToken(user);

        int count = Integer.parseInt(new D100().roll().getRolls()[0], 10);
        if ("valid#0001".equalsIgnoreCase(user.getName())) {
            count = 0;
        }

        List<OwnedProduct> result = new ArrayList<>(count);

        for (int i = 1; i < count + 1; i++) {
            result.add(
                    ImmutableOwnedProduct.builder()
                            .id(Integer.toString(i, 10))
                            .name("Product Nr. " + i)
                            .coverURL("https://nowhere/product/" + i + "/cover")

                            .datePurchased(OffsetDateTime.now(UTC).minusMonths(3L))
                            .isArchived("0")
                            .build()
            );
        }

        LOG.trace("Created owned products: user='{}', count={}", user.getDisplayName(), result.size());
        return result;
    }
}
