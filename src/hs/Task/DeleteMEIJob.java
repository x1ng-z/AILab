package hs.Task;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import hs.Configuartion.SpringAnnotationConfigure;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/27 9:14
 */
@Component
public class DeleteMEIJob {
    private static final Logger logger = Logger.getLogger(DeleteMEIJob.class);
    @Value("${meidir}")
    @NonNull
    private String meidir;

    public boolean deletePath(String meidir) {
        StringBuffer path = new StringBuffer();
        path.append(meidir);
//        path.append(File.separator);
//        path.append(transactionDate);
//        path.append(File.separator);
//        path.append(batchNo);
//        path.append(File.separator);
        File file = new File(path.toString());
//        System.out.println("路径："+ path.toString());
        //方法一
        /*try {
            org.apache.commons.io.FileUtils.deleteDirectory(file);
            return true;
        } catch (IOException e) {
            System.out.println("删除异常！");
            return false;
        }*/
        //方法二
        return deleteDir(file);
    }


    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return
     */
    private static boolean deleteDir(File dir) {
        if (!dir.exists()) {
            return false;
        }
        if (dir.isDirectory()) {
            String[] childrens = dir.list();
            // 递归删除目录中的子目录下
            for (String child : childrens) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
//                    return false;
                    continue;
                } else {
//                    logger.debug("success delete " + child);
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }


    //        public static void main(String[] args) {
//        ;
//            if (new DeleteMEIJob().deletePath("000017PINC0000000962", "2017-10-09")) {
//                System.out.println("删除成功");
//            }else {
//                System.out.println("删除失败");
//            }
//
//        }
//    @Scheduled(fixedRate = 1000, initialDelay = 1000)
//    @Async
//    public void test() {
//        logger.info(meidir+"删除成功" + Instant.now());
//    }


    @Scheduled(fixedRate = 1000 * 60 * 30,initialDelay = 3000)
    @Async
    public void deletmeitempdir() {
        try {
            if (deletePath(meidir)) {
                logger.info("delete IME files success");
            }else {
                logger.warn("delete IME filesfailed");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}
