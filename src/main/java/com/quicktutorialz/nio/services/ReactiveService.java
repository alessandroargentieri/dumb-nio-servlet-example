package com.quicktutorialz.nio.services;

import com.quicktutorialz.nio.entities.User;
import com.quicktutorialz.nio.entities.UserData;
import io.reactivex.Flowable;

import java.util.concurrent.Callable;

public class ReactiveService {



    public Callable<String> getData(String name) throws Exception {
        Callable<String> futureData = () -> dataTask(name);
        return futureData;
    }

    public Flowable<String> getFlowableData(String name){
        try {
            Thread.sleep(10000);
        }catch (InterruptedException ie){ ie.printStackTrace(); }
        return Flowable.fromCallable( () -> dataTask(name));
    }

    private String dataTask(String name) throws InterruptedException {
        Thread.sleep(1000);
        return "This data come from Dao for you " + name;
    }

    public Flowable<User> getFlowableUser(final UserData userData){
        try {
            Thread.sleep(10000);
        }catch (InterruptedException ie){ ie.printStackTrace(); }
        User user = new User(userData.getName()+" "+userData.getSurname());
        return Flowable.just(user);

    }
}
