# minetime #
## description ##
* minecraft bukkit plugin for player time spent
on a server for some amout of days (<=33);
    - "minetime" command to get it;
* mongo database use for player time data;
## languages ##
code | usefor
---- | ------
java | source
yml  | config
xml  | config
## environment ##
* this is set up primarly for portable vscode;
    * using java extension pack we are able to get fully featured environment:
        - code highlight and intellisense;
        - compile time checking;
        - easy debugging;
* support for maven projects, and for intellij idea as well;
## debug ##
* to start debugging a plugin at runtime, we need:
    * launch the paper server with specific java arguments:
        - "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=...some_number..."
    * attach debugger to the same address:
        - for vscode: make a debug task for vscode with the "attach" request and port defined;