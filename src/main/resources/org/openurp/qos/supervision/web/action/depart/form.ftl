[@b.head/]
[@b.toolbar title="领导听课记录表填写(草)" ]
  bar.addClose();
[/@]
 <div class="container-fluid" id="data" width="100%" align="center" cellpadding="0" cellspacing="0">
   <div align='center'><h4>${assessor.school.name}领导听课记录表</h4></div>
   <div align='center'>${clazz.semester.schoolYear}年度 [#if clazz.semester.name='1']第一学期[#elseif clazz.semester.name='2']第二学期[#else]${clazz.semester.name}[/#if]</div>
   [@b.form name="supervingForm" id="supervingForm"  action="!submit" theme="html"]
   <input type="hidden" name="clazz.id" value="${supervision.clazz.id}"/>
   <input type="hidden" name="supervisionForm.id" value="${supervisionForm.id}"/>
   <input type="hidden" name="assessor.id" value="${assessor.id}"/>
   <table width='90%' align='center' border='0' style="font-size:13px">
    <tr>
      <td width='50%'>开课院系:${clazz.teachDepart.name}</td>
      <td width='50%' id="f_assessOn">听课日期：
        <select name="schedule">
          [#list schedules as schedule]
          <option value="${schedule.date}_${schedule.units}" [#if (schedule.date)?string('yyyy-MM-dd')=(supervision.assessOn)!"--"]selected="selected"[/#if]>${schedule.date} ${schedule.room}</option>
          [/#list]
        </select>
        第
        <select name="courseUnit" style="width:50px">
          [#list units as u]
          <option value="${u}" [#if u=(supervision.courseUnit)!0]selected="selected"[/#if]>${u}</option>
          [/#list]
        </select>小节
      </td>
    </tr>
    <tr>
      <td>课程代码:${clazz.course.code}</td>
      <td>课程序号:${clazz.crn?if_exists}</td>
    </tr>
    <tr>
      <td>课程名称:${clazz.course.name}</td>
      <td>任课教师:
        <select name="teacher.id" style="width:150px">
         [#list clazz.teachers as t]
         <option value="${t.id}" [#if t.id=(supervision.teacher.id)!0]selected="selected"[/#if]>${t.name}</option>
         [/#list]
        </select>
      </td>
    </tr>
    <tr>
      <td>教师是否准时上课、下课：<input name="supervision.teachingOnTime" [#if supervision.teachingOnTime]checked[/#if] type="checkbox" style="width:50px"></td>
      <td id="f_lateStdCount">迟到学生人数：<input name="supervision.lateStdCount" value="${supervision.lateStdCount}"  type="text" style="width:50px"></td>
    </tr>
   </table>
   <table style="width: 90%;" class="grid-table">
     [#list supervisionForm.fields?sort_by("indexNo") as field]
       [#if !field.selective && !(field.indicator??)]
      <tr>
        <td id="f_field_${field.id}"><B>${field.name}</B></td>
      </tr>
      <tr>
        <td style="height:120px">
        <textarea name="field_${field.id}" style="width:100%;height:100%">${supervision.getText(field)!}</textarea>
        </td>
      </tr>
      [/#if]
    [/#list]
   </table>
   <table class="grid-table" style="width: 90%;text-align:center;">
     <tr>
       <td width="10%"><B>类别</B></td>
       <td colspan="2" style="width: 40%;"><B>评价项目</B></td>
       [#list supervisionForm.grades?sort_by("maxScore")?reverse as op]
       <td><B>${op.name}</B>(${op.minScore}~${op.maxScore})</td>
       [/#list]
     </tr>
     [#assign typeFields={}/]

     [#list supervisionForm.fields as field]
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
         [#assign fieldScore=supervision.getScore(field)/]
         [#if field.selective]
           [#list supervisionForm.grades?sort_by("maxScore")?reverse as op]
            <td rowspan="${typeFields[typeName]?size}">
              [#if op.maxScore != op.minScore]
                <input type="number"  name="field_${field.id}_score" id="field_${field.id}_${op_index}_score"  placeholder="${op.minScore}~${op.maxScore}"  min="${op.minScore}" max="${op.maxScore}"
                  [#if op.contains(fieldScore)] style="width:80px" value="${fieldScore}"[#else]style="width:80px;display:none"[/#if]/>
                <input type="radio" name="field_${field.id}" id="field_${field.id}_${op_index}" onclick="displayScore('field_${field.id}','field_${field.id}_${op_index}')" id="field_${field.id}_${op_index}" value="${op.minScore}" [#if op.contains(fieldScore)] checked="checked"[/#if]/> <label for="field_${field.id}_${op_index}">${op.name}</label>
              [#else]
                <input type="radio" name="field_${field.id}" id="field_${field.id}_${op_index}" value="${op.minScore}" [#if op.contains(fieldScore)] checked="checked"[/#if]/> <label for="field_${field.id}_${op_index}">${op.name}</label>
              [/#if]
            </td>
           [/#list]
         [/#if]
        </tr>
      [/#list]
    [/#list]

   </table>

   <br>
   <table width='90%' >
     <tr>
       <td width="33%"><B>听课人:</B>${assessor.name}</td>
       <td width="34%"><B>单位:</B>${(assessor.department.name)!}</td>
     </tr>
      <tr align="center">
      <td colspan="2">
          <input type="button" onClick='save(this.form)' value="提交" class="buttonStyle"/>
      </td>
    </tr>
   </table>
[/@]
</div>

<script type="text/javascript">
   [#assign scoreFields=[]/]
   [#list supervisionForm.fields as f]
     [#if f.selective || f.score??]
     [#assign scoreFields = scoreFields +[f] /]
     [/#if]
   [/#list]
    function save(form){
        form.submit();
    }

   function displayScore(fieldId,radioId){
      var scoreFields=document.supervingForm[fieldId+"_score"];
      for(var i=0;i <scoreFields.length;i++){
        var field=scoreFields[i]
        field.value='';
        field.style.display="none";
      }
      if(document.getElementById(radioId).checked){
        document.getElementById(radioId+"_score").style.display=""
      }else{
        document.getElementById(radioId+"_score").style.display="None"
      }
   }
 </script>
[@b.foot/]
