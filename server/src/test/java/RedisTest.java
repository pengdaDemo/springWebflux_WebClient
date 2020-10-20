import com.pd.ServerApplication;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = ServerApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void test()throws Exception{
        //stringRedisTemplate.opsForList().leftPush("crawler_migrateSeedId","hfuwdifhwkefhekfh");
        //stringRedisTemplate.opsForList().leftPush("crawler_migrateSeedId","dcbuysvbkjvbjnaskhd");
        //stringRedisTemplate.opsForList().remove("crawler_migrateSeedId", 0 ,"dcbuysvbkjvbjnaskhd");
        String dataStr = "20200801";

        SimpleDateFormat sim =new SimpleDateFormat("yyyyMMdd");
        long time = sim.parse(dataStr).getTime();
        String str=dataStr.substring(0, 6);
        long num = 0;
        while (true) {
            dataStr = sim.format(new Date(time));
            String ss = dataStr.substring(0, 6);
            if(!ss.equals(str)){
                break;
            }
            String ssk = stringRedisTemplate.opsForValue().get("crawlDayCount"+dataStr);
            if(ssk == null)break;
            int count = Integer.parseInt(ssk);
            System.out.println(dataStr+"      "+count);
            num+= count;
            time += 86400000;
        }
        System.out.println(num);
    }
    @Test
    public void exireTime() throws Exception{

        String key = "seedUpdateControlCount";
        long nowTime = new Date().getTime() - 86400000;
        for (int i = 0; i < 4000; i++) {
            String dateStr = DateFormatUtils.format(new Date(nowTime+i*60*1000), "HH:mm MM-dd");
            stringRedisTemplate.opsForValue().set(key + dateStr, String.valueOf((int)(Math.random()*20000)), 1, TimeUnit.DAYS);
        }


    }
}
