//import com.carrotsearch.sizeof.RamUsageEstimator;
//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import com.pd.ServerApplication;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.openjdk.jol.info.ClassLayout;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.concurrent.ConcurrentMap;
//import java.util.concurrent.TimeUnit;
//
//@SpringBootTest(classes = ServerApplication.class)
//@RunWith(SpringRunner.class)
//public class GuavaTest {
//    public Cache<String, Object> cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
//    @Test
//    public void test() throws Exception{
//        cache.put("test","vbusbviiedkzjsevdfhvkbnfkdbh;vjdofvhfj;nfddivhnffvbfbdbhdbbobidoibhfobhfobhdoihbbhobhfhofbffhbfodbhfbhfdofhbhfdofibhdobhdibfbdiobodfibhdobhfidygvisldvhudvbtgmujixfbhiufvknzilcdscvukvjuyyyyyyyyyyyyyyyyyyyaeigkgbxcygefdevjwcfbkucbejkbmcewkegbcdysgcerfbjs,vbdkucsdkcbwejcbskjdbv ksduhvgfgeuwdgeywdedcgbikfgsigfgvbsifgvcdyugbvysdrfuvsdriufgrsdifgvbifgvbifgviugvfgvsiufgif");
//        Thread.sleep(5000);
//        System.out.println(cache.size());
//        System.out.println(cache.getIfPresent("test"));
//        Thread.sleep(6000);
//        //cache.cleanUp();
//        System.out.println(RamUsageEstimator.sizeOf(cache));
//        //cache.put("test2","vfdhyszeklzvhfudziohdfcdAZ;hifhruzgvdfugvkbsjbhfukxdfbhskjbufhvbdfjkbsdbnhxcsduicfgbdhmvbsvbduivnlsnvekvm,sdvnjevnsekfksl,mclvn.ndkslvndfvkvntsadjcgygcsedkcbhdksvksdhjklvhgudhhcvdjvnf,vnfvshjvdklf");
//        //System.out.println(RamUsageEstimator.sizeOf(cache));
//        System.out.println(cache.size());
//        cache.cleanUp();
//        System.out.println(RamUsageEstimator.sizeOf(cache));
//        System.out.println(cache.size());
//        ConcurrentMap<String, Object> map = cache.asMap();
//        System.out.println(map);
//        System.out.println(map.size());
//        //System.out.println(cache.getIfPresent("test"));
//    }
//}
