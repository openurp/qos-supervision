/*
 * Copyright (C) 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openurp.qos.supervision.model

import org.beangle.data.orm.MappingModule

class DefaultMapping extends MappingModule {
  def binding(): Unit = {
    bind[SupervisingLevel]
    bind[Supervisor]

    bind[SupervisionGrade]
    bind[SupervisionField]
    bind[SupervisionIndicator]
    bind[SupervisionForm] declare { e =>
      e.fields is depends("form")
      e.grades is depends("form")
    }

    bind[Supervision] declare { e =>
      e.texts is depends("supervision")
      e.selects is depends("supervision")
    }
    bind[SupervisionText]
    bind[SupervisionSelect]

    bind[SupervisionClazzCategory]
    bind[SupervisionClazz]
  }
}
