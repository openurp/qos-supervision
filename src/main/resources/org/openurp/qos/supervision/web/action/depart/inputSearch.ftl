[#ftl]
[@b.head/]
[@b.toolbar title="听课录入"]bar.addClose();[/@]
  [@b.form theme="list" action="!input"]
    [@b.select name="supervisor.id" label="听课人" items=supervisors required="true"/]
    [@b.select name="clazz.id" label="听课课程" href="!clazzes?q={term}&clazz.semester.id="+semester.id required="true" style="width:400px"/]
    [@b.formfoot]
      [@b.submit value="开始录入"/]
      <input name="supervision.clazz.semester.id" type="hidden" value=""/>
    [/@]
  [/@]
[@b.foot/]
