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

import org.beangle.commons.collection.Collections
import org.beangle.data.model.LongId
import org.beangle.data.model.pojo.{Named, TemporalOn, Updated}
import org.openurp.base.hr.model.Teacher
import org.openurp.base.model.User
import org.openurp.edu.clazz.model.Clazz

import java.time.{LocalDate, LocalTime}
import scala.collection.mutable

/** 督导评价表
 */
class SupervisionForm extends LongId, TemporalOn, Named {
  var fields: mutable.Buffer[SupervisionField] = Collections.newBuffer[SupervisionField]
  var grades: mutable.Buffer[SupervisionGrade] = Collections.newBuffer[SupervisionGrade]
}

/** 督导评价项目
 */
class SupervisionField extends LongId, Named {
  var indexNo: Int = _
  var form: SupervisionForm = _
  var parent: Option[SupervisionField] = None
  var indicator: Option[SupervisionIndicator] = None
  var label: String = _

  var score: Option[Int] = None
  var selective: Boolean = _
  var maxlength: Int = _
}

/** 督导评价指标类型
 */
class SupervisionIndicator extends LongId, Named

/** 督导评价级别
 */
class SupervisionGrade extends LongId, Named {
  var form: SupervisionForm = _
  var minScore: Int = _
  var maxScore: Int = _

  def contains(score: Int): Boolean = minScore <= score && score <= maxScore

}

/** 督导评价--结果
 */
class Supervision extends LongId, Updated {
  var level: SupervisingLevel = _
  var form: SupervisionForm = _
  var clazz: Clazz = _
  var teacher: Teacher = _
  var assessor: User = _
  var assessOn: LocalDate = _
  var room: String = _
  var courseUnit: Int = _
  var lessonStdCount: Int = _
  var lateStdCount: Int = _
  var teachingOnTime: Boolean = _

  var score: Int = _

  var texts: mutable.Buffer[SupervisionText] = Collections.newBuffer[SupervisionText]
  var selects: mutable.Buffer[SupervisionSelect] = Collections.newBuffer[SupervisionSelect]

  def getText(field: SupervisionField): String = {
    texts.find(_.field == field).map(_.contents).getOrElse("")
  }

  def setText(field: SupervisionField, text: String): Unit = {
    texts.find(_.field == field) match
      case None =>
        val newer = new SupervisionText
        newer.supervision = this
        newer.field = field
        newer.contents = text
        this.texts += newer
      case Some(t) => t.contents = text
  }

  def getScore(field: SupervisionField): Int = {
    selects.find(_.field == field).map(_.score).getOrElse(0)
  }

  def setScore(field: SupervisionField, score: Int): Unit = {
    selects.find(_.field == field) match
      case None =>
        val newer = new SupervisionSelect
        newer.supervision = this
        newer.field = field
        newer.score = score
        this.selects += newer
      case Some(s) => s.score = score
  }
}

/** 督导评价--选择题
 */
class SupervisionSelect extends LongId {
  var field: SupervisionField = _
  var score: Int = _
  var supervision: Supervision = _
}

/** 督导评价--文字题
 */
class SupervisionText extends LongId {
  var field: SupervisionField = _
  var contents: String = _
  var supervision: Supervision = _
}
