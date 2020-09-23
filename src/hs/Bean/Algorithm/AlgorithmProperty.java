package hs.Bean.Algorithm;

import hs.Bean.ModleProperty;
import hs.Bean.PictureJPG;
import hs.Controller.AIModleController;
import hs.Filter.Filter;
import org.apache.log4j.Logger;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/31 21:45
 */
public class AlgorithmProperty implements ModleProperty {
    public static Logger logger = Logger.getLogger(AlgorithmProperty.class);

    public static final String DATATYPE_IMAGE="image";//属性值类型
    public static final String DATATYPE_VALUE="value";


    private int propertyid;
    private String propertyName;// '属性名称(ch-zh)',中文注解
    private String property ;//'属性',
    private int refrencealgorithmid;// '引用的算法id',
    private String resource ;//'opc数据源'
    private String opctag;
    private String datatype;

    private Filter filter=null;

    private double value=0;
    private PictureJPG pictureJPG;


    public  double getPropertyValue(){

        /**常量数据直接转换提起就行*/
        if (resource.equals("constant")) {
            /***常数*/
            return Double.valueOf(opctag);
        }

        /**有滤波器*/
        if(filter!=null){
            Double filterresult = filter.getLastfilterdata();
            if (filterresult != null) {
                logger.debug("filter is good");
                return filterresult;
            } else {
                return (value);
            }
            /**无滤波器*/
        }else {
            return value;
        }

    }


    public int getPropertyid() {
        return propertyid;
    }

    public void setPropertyid(int propertyid) {
        this.propertyid = propertyid;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public int getRefrencealgorithmid() {
        return refrencealgorithmid;
    }

    public void setRefrencealgorithmid(int refrencealgorithmid) {
        this.refrencealgorithmid = refrencealgorithmid;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getOpctag() {
        return opctag;
    }

    public void setOpctag(String opctag) {
        this.opctag = opctag;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public PictureJPG getPictureJPG() {
        return pictureJPG;
    }

    public void setPictureJPG(PictureJPG pictureJPG) {
        this.pictureJPG = pictureJPG;
    }

    @Override
    public String getOPCTAG() {
        return opctag;
    }
}
