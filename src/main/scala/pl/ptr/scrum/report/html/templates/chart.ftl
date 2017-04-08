<div class="panel panel-default" style="padding:20px; overflow: auto; margin: auto;">
    <div class="page-header">
        <h4>Sprint ${sprintNumber} (${dateFrom}-${dateTo}), ${name} </h4>
    </div>

    <#include "burndown.ftl">
    <#include "flow.ftl">
    <#include "pie.ftl">

    <div style="clear:both"></div>
    <#include "round.ftl">
    <div style="clear:both"></div>

    <#include "start.ftl">
    <#include "done.ftl">
    <#include "worklog.ftl">


</div>

<div style="margin: 20px; clear: both;"></div>

