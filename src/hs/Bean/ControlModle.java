package hs.Bean;

import com.alibaba.fastjson.JSONObject;
import hs.ApcAlgorithm.ExecutePythonBridge;
import hs.Opc.OPCService;

import java.util.*;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 8:24
 */
public class ControlModle {
    private OPCService OPCserver;
    private ExecutePythonBridge executePythonBridge;
    private int modleId;
    private String modleName;

    private List<ModleTag> unhandleTag;
    private List<FFModleTag> feedForwards=new ArrayList<>();//FF前馈feedforward
    private List<MVModleTag> mVariables =new ArrayList<>();//MV
    private List<SPModleTag> setpointVariables=new ArrayList<>();//SP

//    private Map<Integer,List<ModleTag>> keyPV_valueMVModleTag_Map =new HashMap<>();
//    private Map<Integer,List<ModleTag>> keyPV_valueFFModleTag_Map =new HashMap<>();
    private Integer predicttime=12;//预测时域
    private Integer outpoints=0;//输出个数量
    private Integer controltime=6;//单一控制输入未来控制M步增量(控制域)
    private Integer inputpoints=0;//可控制输入数量
    private Integer feedforwardpoints=0;//前馈数量
    private Integer timeserisN=40;
    private Integer sampleStep=0;

    private List<PVModleTag> categoryPVmodletag=new ArrayList<>();
    private List<SPModleTag> categorySPmodletag=new ArrayList<>();
    private List<MVModleTag> categoryMVmodletag=new ArrayList<>();
    private List<FFModleTag> categoryFFmodletag=new ArrayList<>();




    private LinkedHashSet<Integer> mvsort=new LinkedHashSet<>();//tagid
    private LinkedHashSet<Integer> pvsort=new LinkedHashSet<>();//tagid
    private LinkedHashSet<Integer> ffsort=new LinkedHashSet<>();//tagid
    private LinkedHashSet<Integer> spsort=new LinkedHashSet<>();//tagid

    private Double[][][] mvtimeserise=null;//输入响应

    private Double[][][] fftimeserise=null;//前馈响应



    /**
     * 目标值(sp)
     * 前馈变化值幅度
     *mv范围
     * pv范围
     * */



    public void realizeModle(){

        /**
         * 将标签分类，并且把pv ff mv 的顺序插入
         * */
        for(ModleTag tag:unhandleTag){

            if(tag instanceof FFModleTag){
                categoryFFmodletag.add((FFModleTag)tag);
                ffsort.add(tag.getTagclazz().getTagId());
            }

            if(tag instanceof MVModleTag){
                categoryMVmodletag.add((MVModleTag)tag);
                mvsort.add(tag.getTagclazz().getTagId());
                sampleStep=tag.getSample_step();

                if(OPCserver.getOpctags().get(tag.getTagclazz().getTagId()).getMaxlimit()==null){
                    TagLimit tagMaxLimit=new TagLimit();
                    tagMaxLimit.setValue(tag.getLimitHigh());
                    tagMaxLimit.setResource(tag.getLimitHighRes());

                    TagLimit tagMinLimit=new TagLimit();
                    tagMinLimit.setValue(tag.getLimitLow());
                    tagMinLimit.setResource(tag.getLimitLowRes());

                    OPCserver.getOpctags().get(tag.getTagclazz().getTagId()).setMinlimit(tagMinLimit);
                    OPCserver.getOpctags().get(tag.getTagclazz().getTagId()).setMaxlimit(tagMaxLimit);
                    timeserisN=tag.getResponTimeSeries().length;
                }


            }

            if(tag instanceof SPModleTag){
                categorySPmodletag.add((SPModleTag)tag);
            }

            if(tag instanceof PVModleTag){
                categoryPVmodletag.add((PVModleTag)tag);
                pvsort.add(tag.getTagclazz().getTagId());

                if(OPCserver.getOpctags().get(tag.getTagclazz().getTagId()).getMaxlimit()==null){
                    TagLimit tagMaxLimit=new TagLimit();
                    tagMaxLimit.setValue(tag.getLimitHigh());
                    tagMaxLimit.setResource(tag.getLimitHighRes());

                    TagLimit tagMinLimit=new TagLimit();
                    tagMinLimit.setValue(tag.getLimitLow());
                    tagMinLimit.setResource(tag.getLimitLowRes());

                    OPCserver.getOpctags().get(tag.getTagclazz().getTagId()).setMinlimit(tagMinLimit);
                    OPCserver.getOpctags().get(tag.getTagclazz().getTagId()).setMaxlimit(tagMaxLimit);
                }
            }

        }

        /**
         *初始化sp的
         * */
        for(PVModleTag pvModleTag:categoryPVmodletag){

            for(SPModleTag spModleTag:categorySPmodletag){
                if(pvModleTag.getTagclazz().getTagId()==spModleTag.getPvTag().getTagId()){
                    spsort.add(spModleTag.getTagclazz().getTagId());
                }

            }

        }

        /**
         *初始化 模型参数
         *     private Integer predicttime=12;//预测时域
         *     private Integer outpoints=0;//输出个数量
         *     private Integer controltime=6;//单一控制输入未来控制M步增量(控制域)
         *     private Integer inputpoints=0;//可控制输入数量
         *     private Integer feedforwardpoints=0;//前馈数量
         *     private Integer timeserisN=40;
         * */

        predicttime=12;
        outpoints=pvsort.size();
        controltime=6;
        inputpoints=mvsort.size();
        feedforwardpoints=ffsort.size();

        if(inputpoints!=0){
            mvtimeserise=new Double[outpoints][inputpoints][timeserisN];//输入响应
        }

        if(feedforwardpoints!=0){
            fftimeserise=new Double[outpoints][feedforwardpoints][timeserisN];//前馈响应
        }

        int pvi=0;

        for(PVModleTag pvModleTag:categoryPVmodletag){
            int mvi=0;
            //输入响应
            for(int i=0;i<categoryMVmodletag.size();i++){
                for(MVModleTag mvModleTag:categoryMVmodletag){
                    if((pvModleTag.getTagclazz().getTagId()==mvModleTag.getPvTag().getTagId())&&(mvModleTag.getTagclazz().getTagId()==new ArrayList<Integer>(mvsort).get(mvi))){
                        mvtimeserise[pvi][mvi]=mvModleTag.getResponTimeSeries();
                        mvi++;
                    }

                }
                if(mvi==categoryMVmodletag.size()){
                    break;
                }
            }



            int ffi=0;
            for(int i=0;i<categoryFFmodletag.size();i++){
                for(FFModleTag ffModleTag:categoryFFmodletag){
                    if((pvModleTag.getTagclazz().getTagId()==ffModleTag.getPvTag().getTagId())&&(ffModleTag.getTagclazz().getTagId()==new ArrayList<Integer>(ffsort).get(ffi))){
                        fftimeserise[pvi][ffi]=ffModleTag.getResponTimeSeries();
                        ffi++;
                    }
                }
                if (ffi==categoryFFmodletag.size()){
                    break;
                }
            }

            pvi++;

        }

    }



    public JSONObject getrealData(){
        /**
         * y0(pv)
         * limitU(mv)
         * limitY()
         * detaFF
         * Wi(sp)
         * */

        JSONObject jsonObject=new JSONObject();

        //sp
        Double[] sp=new Double[spsort.size()];
        int loop=0;
        for(Integer tgid:spsort){
            sp[loop]= OPCserver.readTagvalue(tgid);
            loop++;
        }
        jsonObject.put("wi",sp);

        //pv
        Double[] pv=new Double[pvsort.size()];
        loop=0;
        for(Integer tgid:pvsort){
            pv[loop++]= OPCserver.readTagvalue(tgid);
        }
        jsonObject.put("y0",pv);

        //limitU输入限制
        Double[][] limitU=new Double[mvsort.size()][2];
        loop=0;
        //U当前输入
        Double[] U=new Double[mvsort.size()];
        for(Integer tgid:mvsort){

            Tag tag= OPCserver.getOpctags().get(tgid);
            if(tag!=null){
                Double[] minmax=new Double[2];

                TagLimit tagMinlimita=tag.getMinlimit();
                if(tagMinlimita.getResource().equals("constant")){
                    minmax[0]=Double.valueOf(tagMinlimita.getValue());
                }else if(tagMinlimita.getResource().equals("opc")){
                    minmax[0]= OPCserver.getOpctags().get(Integer.valueOf(tagMinlimita.getValue())).getNewvalue();
                }

                TagLimit tagMaxlimit=tag.getMaxlimit();
                if(tagMaxlimit.getResource().equals("constant")){
                    minmax[1]=Double.valueOf(tagMaxlimit.getValue());
                }else if(tagMaxlimit.getResource().equals("opc")){
                    minmax[1]= OPCserver.getOpctags().get(Integer.valueOf(tagMaxlimit.getValue())).getNewvalue();
                }

                limitU[loop]=minmax;

            }

            U[loop]=tag.getNewvalue();
            loop++;

        }
        jsonObject.put("limitU",limitU);
        jsonObject.put("U",U);

        //limitY

        Double[][] limitY=new Double[pvsort.size()][2];
        loop=0;

        for(Integer tgid:pvsort){
           Tag tag= OPCserver.getOpctags().get(tgid);
           if(tag!=null){
               Double[] minmax=new Double[2];

               TagLimit tagMinlimit=tag.getMinlimit();
               if(tagMinlimit.getResource().equals("constant")){

                   minmax[0]=Double.valueOf(tagMinlimit.getValue());

               }else if(tagMinlimit.getResource().equals("opc")){

                   minmax[0]= OPCserver.getOpctags().get(Integer.valueOf(tagMinlimit.getValue())).getNewvalue();

               }

               TagLimit tagMaxlimit=tag.getMaxlimit();
               if(tagMaxlimit.getResource().equals("constant")){

                   minmax[1]=Double.valueOf(tagMaxlimit.getValue());

               }else if(tagMaxlimit.getResource().equals("opc")){

                   minmax[1]= OPCserver.getOpctags().get(Integer.valueOf(tagMaxlimit.getValue())).getNewvalue();

               }
               limitY[loop++]=minmax;

           }


        }

        jsonObject.put("limitY",limitY);


        //deltaFF
        loop=0;
        if(ffsort.size()!=0){
            Double[] deltff=new Double[ffsort.size()];
            for(Integer tagid:ffsort){
                deltff[loop]=OPCserver.readTagDeltaValue(tagid);
                loop++;
            }

            jsonObject.put("deltff",deltff);

        }




        return jsonObject;

    }

    public boolean writeData(Integer tgid,Double value){
        return OPCserver.writeTagvalue(tgid,value);
    }


    public Integer getPredicttime() {
        return predicttime;
    }

    public void setPredicttime(Integer predicttime) {
        this.predicttime = predicttime;
    }

    public Integer getOutpoints() {
        return outpoints;
    }

    public void setOutpoints(Integer outpoints) {
        this.outpoints = outpoints;
    }

    public Integer getControltime() {
        return controltime;
    }

    public void setControltime(Integer controltime) {
        this.controltime = controltime;
    }

    public Integer getInputpoints() {
        return inputpoints;
    }

    public void setInputpoints(Integer inputpoints) {
        this.inputpoints = inputpoints;
    }

    public Integer getFeedforwardpoints() {
        return feedforwardpoints;
    }

    public void setFeedforwardpoints(Integer feedforwardpoints) {
        this.feedforwardpoints = feedforwardpoints;
    }

    public Integer getTimeserisN() {
        return timeserisN;
    }
    public void setTimeserisN(Integer timeserisN) {
        this.timeserisN = timeserisN;
    }
    public String getModleName() {
        return modleName;
    }

    public void setModleName(String modleName) {
        this.modleName = modleName;
    }

    public List<FFModleTag> getFeedForwards() {
        return feedForwards;
    }

    public void setFeedForwards(List<FFModleTag> feedForwards) {
        this.feedForwards = feedForwards;
    }

    public List<MVModleTag> getmVariables() {
        return mVariables;
    }

    public void setmVariables(List<MVModleTag> mVariables) {
        this.mVariables = mVariables;
    }

    public List<SPModleTag> getSetpointVariables() {
        return setpointVariables;
    }

    public void setSetpointVariables(List<SPModleTag> setpointVariables) {
        this.setpointVariables = setpointVariables;
    }

    public List<ModleTag> getUnhandleTag() {
        return unhandleTag;
    }

    public void setUnhandleTag(List<ModleTag> unhandleTag) {
        this.unhandleTag = unhandleTag;
    }

    public int getModleId() {
        return modleId;
    }

    public void setModleId(int modleId) {
        this.modleId = modleId;
    }


    public void setOPCserver(OPCService OPCserver) {
        this.OPCserver = OPCserver;
    }


    public Double[][][] getMvtimeserise() {
        return mvtimeserise;
    }

    public Double[][][] getFftimeserise() {
        return fftimeserise;
    }

    public LinkedHashSet<Integer> getMvsort() {
        return mvsort;
    }

    public LinkedHashSet<Integer> getPvsort() {
        return pvsort;
    }

    public LinkedHashSet<Integer> getFfsort() {
        return ffsort;
    }

    public LinkedHashSet<Integer> getSpsort() {
        return spsort;
    }

    public void setExecutePythonBridge(ExecutePythonBridge executePythonBridge) {
        this.executePythonBridge = executePythonBridge;
    }

    public ExecutePythonBridge getExecutePythonBridge() {
        return executePythonBridge;
    }

    public Integer getSampleStep() {
        return sampleStep;
    }
}
