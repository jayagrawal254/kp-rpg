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

package de.kaiserpfalzedv.rpg.integrations.datastore.resources;

import de.kaiserpfalzedv.rpg.core.resources.ImmutableResourcePointer;
import de.kaiserpfalzedv.rpg.core.resources.ResourcePointer;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.beans.Transient;
import java.util.UUID;

/**
 * A generic resource pointer.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.2.0  2021-01-31
 */
public class MongoResourcePointer {
    public String kind;
    public String apiVersion;

    public String nameSpace;
    public String name;
    public UUID uid;

    public MongoResourcePointer() {}

    public MongoResourcePointer(final ResourcePointer orig) {
        kind = orig.getKind();
        apiVersion = orig.getApiVersion();

        nameSpace = orig.getNamespace();
        name = orig.getName();
    }

    @BsonIgnore
    @Transient
    public ResourcePointer pointer() {
        return ImmutableResourcePointer.builder()
                .kind(kind)
                .apiVersion(apiVersion)

                .namespace(nameSpace)
                .name(name)

                .uid(uid)

                .build();
    }
}
