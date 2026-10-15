package org.linlinjava.litemall.core.task;

import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

/**
 * 延迟任务调度服务。
 *
 * <p>内部使用 {@link DelayQueue} 保存待执行的延迟任务，并在启动时启动一个常驻线程
 * 消费队列中的到期任务；对外提供任务的添加与移除能力。
 */
@Component
public class TaskService {

    /** 延迟任务队列，任务按到期时间排序 */
    private DelayQueue<Task> delayQueue =  new DelayQueue<Task>();

    /**
     * 启动任务消费线程。
     *
     * <p>单线程常驻消费 {@link DelayQueue}，任务到期即执行；循环仅捕获 InterruptedException；任务抛出未捕获的运行时异常可能终止消费。
     */
    @PostConstruct
    private void init() {


        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Task task = delayQueue.take();
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 添加延迟任务，已存在（按 id 相等）的任务不重复添加
     *
     * @param task 待添加的延迟任务
     */
    public void addTask(Task task){
        if(delayQueue.contains(task)){
            return;
        }
        delayQueue.add(task);
    }

    /**
     * 移除延迟任务
     *
     * @param task 待移除的延迟任务
     */
    public void removeTask(Task task){
        delayQueue.remove(task);
    }

}
