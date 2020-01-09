package org.linlinjava.litemall.core;

import com.google.common.primitives.Ints;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.core.task.Task;
import org.linlinjava.litemall.core.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskTest {
    private final Log logger = LogFactory.getLog(TaskTest.class);
    @Autowired
    private TaskService taskService;

    private class DemoTask extends Task {

        DemoTask(String id, long delayInMilliseconds){
            super(id, delayInMilliseconds);
        }
        @Override
        public void run() {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String now = df.format(LocalDateTime.now());
            System.out.println("task id=" + this.getId() + " at time=" + now);
        }

    }

    @Test
    public void test() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = df.format(LocalDateTime.now());
        System.out.println("start at time=" + now);

        taskService.addTask(new DemoTask("3", 1000));
        taskService.addTask(new DemoTask("2", 2000));
        taskService.addTask(new DemoTask("1", 3000));

        taskService.addTask(new DemoTask("4", 1500));
        taskService.addTask(new DemoTask("5", 2500));
        taskService.addTask(new DemoTask("6", 3500));

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = df.format(LocalDateTime.now());
        System.out.println("start at time=" + now);

        taskService.addTask(new DemoTask("3", 0));
        taskService.addTask(new DemoTask("2", 0));
        taskService.addTask(new DemoTask("1", 0));

        taskService.addTask(new DemoTask("4", 0));
        taskService.addTask(new DemoTask("5", 0));
        taskService.addTask(new DemoTask("6", 0));

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = df.format(LocalDateTime.now());
        System.out.println("start at time=" + now);

        taskService.addTask(new DemoTask("1", 0));
        taskService.addTask(new DemoTask("2", 1200));
        taskService.addTask(new DemoTask("3", 5200));

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
