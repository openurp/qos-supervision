<#include "/templates/head.ftl"/>
 <BODY LEFTMARGIN="0" TOPMARGIN="0" >
  <style>
 .reportTable {
  border-collapse: collapse;
    border:solid;
  border-width:1px;
    border-color:#006CB2;
    vertical-align: middle;
    font-style: normal;
  font-size: 10pt;
}
table.reportTable td{
  border:solid;
  border-width:0px;
  border-right-width:1;
  border-bottom-width:1;
  border-color:#006CB2;

}
 </style>
 <table id="myBar" width="90%"> </table>
 <div id = "data" width="100%" align="center" cellpadding="0" cellspacing="0">
   <div align='center'><h3><@i18nName systemConfig.school/>领导听课记录表</h3></div>
   <div align='center'>${task.calendar.year}年度 <#if task.calendar.term='1'>第一学期<#elseif task.calendar.term='2'>第二学期<#else>${task.calendar.term}</#if></div>
   <table width='90%' align='center' border='0' style="font-size:13px">
    <tr>
      <td width='25%'><@msg.message key="attr.courseNo"/>:${task.course.code}</td>
      <td width='40%'><@msg.message key="attr.courseName"/>:${task.course.name}</td>
      <td align='left'><@msg.message key="entity.courseType"/>:${task.courseType.name}</td>
    </tr>
    <tr>
      <td><@msg.message key="attr.taskNo"/>:${task.seqNo?if_exists}</td>
      <td><@msg.message key="task.arrangeInfo.primaryTeacher"/>:<@getTeacherNames task.arrangeInfo?if_exists.teachers/></td>
      <td align='left'>授课院系:${task.arrangeInfo?if_exists.teachDepart?if_exists.name?if_exists}</td>
    </tr>
   <tr>
      <td>听课时间 ____年__月__日___:___</td>
      <td>教师达到教室____:____&nbsp;开始授课____:___</td>
      <td align='left'>学生应到人数：${task.teachClass.stdCount}&nbsp;实到人数：_____</td>
    </tr>
   <tr>
      <td>听课地点：______________</td>
      <td>迟到人数&nbsp;5分钟以内：_______5分钟以上:_______</td>
      <td align='left'>教师是否对迟到学生提出批评：__________</td>
    </tr>
   </table>
<#if assessForm??>
   <table width='90%' class="listTable">
     <#list assessForm.fields?sort_by("indexNo") as field>
       <#if !field.selective>
      <tr>
        <td><B>${field.name}</B></td>
      </tr>
      <tr>
        <td style="height:200px"></td>
      </tr>
      </#if>
    </#list>
   </table>
   <table width='90%' class="listTable">
     <tr>
       <td width="10%"><B>类别</B></td>
       <td><B>评价项目</B>（请在选定的评价等级上打勾）</td>
       <#list assessForm.options?sort_by("score")?reverse as op>
       <td><B>${op.name}</B><br>${op.score}分</td>
       </#list>
     </tr>
     <#assign typeFields={}/>

     <#list assessForm.fields as field>
       <#if field.selective>
       <#assign typeFields=typeFields + {field.assessType.name:((typeFields[field.assessType.name]!) +[field])}/>
      </#if>
    </#list>

    <#list typeFields?keys as typeName>
      <#list typeFields[typeName]?sort_by("indexNo") as field>
        <tr>
         <#if field_index=0>
         <td rowspan="${typeFields[typeName]?size}">${typeName}</td>
         </#if>
         <td>${field.name}</td>
         <#list 1..assessForm.options?size as op>
          <td>&nbsp;</td>
         </#list>
        </tr>
      </#list>
    </#list>

   </table>
   <br>
   <table width='90%' >
     <tr>
       <td width="33%"><B>听课人:</B>${assessor.name}</td>
       <td width="34%"><B>单位:</B>${(assessor.department.name)!}</td>
       <td width="33%"><B>听课日期:</B>_________________________</td>
     </tr>
   </table>
<#else>
  尚未配置领导听课评价指标！
</#if>

</div>

<script type="text/javascript">
   var bar = new ToolBar("myBar","领导听课记录表打印",null,true,true);
   bar.setMessage('<@getMessage/>');
   bar.addItem("<@msg.message key="action.print"/>","print()");
   bar.addClose();
</script>
 </body>
<#include "/templates/foot.ftl"/>
