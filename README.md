# scrum-report  [![Build Status](https://travis-ci.org/ptr07/scrum-report.svg?branch=master)](https://travis-ci.org/ptr07/scrum-report)
Report tool for scrum teams, based on jira task list export.

### Example report

 <img src=https://github.com/ptr07/scrum-report/blob/master/report.png width=400 height=300 />

### Build

```
sbt clean assembly
```

### Usage

```
Usage: scrum-report [start|data|report] [options] <team1> <team2> ...

  -n, --sprintNumber <value>
                           Enter sprint number
Command: start [options]
Start sprint
  -f, --file <value>       Sprint report file
  -s, --dateFrom <value>   Sprint start date
  -e, --dateTo <value>     Sprint end date
Command: data [options]
Add date from xls report
  -f, --file <value>       Sprint report file
  -d, --date <value>       Report date
  -y, --yesterday          Report date is yesterday
Command: report [options]
Generate html report
  -o, --out <value>        Output file
  <team1> <team2> ...      Teams
  --help                   prints this usage text
```



