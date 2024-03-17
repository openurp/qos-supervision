[@b.head/]
 [@b.toolbar title="领导听课记录表打印"]
   bar.addPrint();
   bar.addBackOrClose();
 [/@]
 [#assign assessor = supervision.assessor/]
 [#assign clazz = supervision.clazz/]
 <div class="container-fluid" id="data" width="100%" align="center" cellpadding="0" cellspacing="0">
   <div align='center'><h4>${supervision.clazz.project.school.name}领导听课记录表</h4></div>
   <div align='center'>${clazz.semester.schoolYear}年度 [#if clazz.semester.name='1']第一学期[#elseif clazz.semester.name='2']第二学期[#else]${clazz.semester.name}[/#if]</div>
   <table width='90%' align='center' border='0' style="font-size：&nbsp;&nbsp;13px">
    <tr>
      <td width='50%'>开课院系：&nbsp;&nbsp;${clazz.teachDepart.name?if_exists}</td>
      <td width='50%' id="f_assessOn">听课日期：&nbsp;&nbsp;${(supervision.assessOn?string('yyyy-MM-dd'))!}</td>
    </tr>
    <tr>
      <td>课程名称：&nbsp;&nbsp;${clazz.course.name}</td>
      <td>主讲教师：&nbsp;&nbsp;${(supervision.teacher.name)!}</td>
    </tr>
    <tr>
      <td>课程代码：&nbsp;&nbsp;${clazz.course.code}</td>
      <td>课程序号：&nbsp;&nbsp;${clazz.crn}</td>
    </tr>
    <tr>
      <td id="f_unit">${supervision.assessOn?string("E")}第${supervision.courseUnit}节</td>
      <td id="f_room">听课地点：&nbsp;&nbsp;${(supervision.room)!}</td>
    </tr>
    <tr>
      <td>教师是否准时上课、下课：&nbsp;&nbsp;${(supervision.teachingOnTime?string('是','否'))!}</td>
      <td>迟到学生人数：&nbsp;&nbsp;${(supervision.lateStdCount)!}</td>
    </tr>
   </table>
   [#assign assessForm = supervision.form/]
   <table class="grid-table" style="width:90%">
     [#list assessForm.fields?sort_by("indexNo") as field]
        [#if !field.selective && !(field.indicator??)]
      <tr>
        <td><B>${field.name}</B></td>
      </tr>
      <tr>
        <td valign="top"><div style="min-height:80px;">${(supervision.getText(field))!}</div></td>
      </tr>
      [/#if]
    [/#list]
   </table>
   <table style="width:90%;text-align:center;" class="grid-table">
     <tr>
       <td width="10%"><B>类别</B></td>
       <td colspan="2" style="width: 40%;"><B>评价项目</B></td>
       [#list assessForm.grades?sort_by("maxScore")?reverse as op]
       <td><B>${op.name}</B><br>(${op.minScore}~${op.maxScore})</td>
       [/#list]
     </tr>
     [#assign typeFields={}/]

     [#list assessForm.fields as field]
       [#if field.indicator??]
       [#assign typeFields=typeFields + {field.indicator.name:((typeFields[field.indicator.name]!) +[field])}/]
       [/#if]
     [/#list]

    [#list typeFields?keys as typeName]
      [#list typeFields[typeName]?sort_by("indexNo") as field]
        <tr>
         [#if field_index=0]
         <td rowspan="${typeFields[typeName]?size}">${typeName}</td>
         [/#if]
         [#if field.label??]
           <td>${field.label} [#if field.score??](${field.score}分)[/#if]</td>
           <td id="f_field_${field.id}">${field.name}</td>
         [#else]
           <td colspan="2" id="f_field_${field.id}">${field.name}</td>
         [/#if]
          [#assign fieldScore=((supervision.getScore(field))!-1)/]
          [#if field.selective]
           [#list assessForm.grades?sort_by("maxScore")?reverse as op]
            <td align="center" rowspan="${typeFields[typeName]?size}">[#if op.contains(fieldScore?int)]${op.name}(${fieldScore})[#else]&nbsp;[/#if]</td>
           [/#list]
          [/#if]
        </tr>
      [/#list]
    [/#list]

   </table>
   <br>
   <table width='90%' >
     <tr>
       <td width="33%"><B>听课人：&nbsp;&nbsp;</B>${assessor.name}</td>
       <td width="34%"><B>单位：&nbsp;&nbsp;</B>${(assessor.department.name)!}</td>
       <td width="33%"><B>听课日期：&nbsp;&nbsp;</B> ${(supervision.assessOn?string('yyyy-MM-dd'))!}</td>
     </tr>
   </table>
</div>

[@b.foot/]
