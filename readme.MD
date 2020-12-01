开发需求
2020.11.12
        新需求开发：
        1、改模拟器为手动比例分配器，新加pv对mv的贡献比例
        关于第一点的说明：
        数据库的modlerespon表中添加effectRatio（作用比例）使用方式如下
                 pv1 ef1
                 pv2 ef2
                 pv1 和pv2都收到mv1的影响，
                 pv1对mv1计算出的dmv1
                 pv2对mv1计算出的dmv2，
                 作用于mv1的pv1部分为dmv1*ef1/(ef1+ef2)

        2数据库表modle添加runstyle 0表示自优化算法运行输出，1表示手动分配pv对mv的影响比例



版本信息:
v1.9.26 更新时候数据库需要设置modlepins表中的pintype(引脚类型)，tracoefmethod(柔化方法)
        同时需要注意pv、ff等相关引脚的上下限位引脚

v1.11.13 更新时候数据库需要设置modlerespon表中的effectRatio(作用比例) 2数据库表modle添加runstyle


2020.12.1已经知道的问题点汇总:
        1、打印日志太多，削减不必要的日志，减少磁盘io
        2、apc是否运行不知道，需要添加apcmain/apcsimulater的运行校验(校验之前需要添加是是否有可以运行的调节的pv)
        3、apc最好还是采用脚本方式运行，不要打包程序exe,占用的磁盘io太多