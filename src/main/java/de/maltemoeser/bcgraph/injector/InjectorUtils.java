package de.maltemoeser.bcgraph.injector;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class InjectorUtils {

    public static Injector getAppInjector() {
        return Guice.createInjector(new AppInjector());
    }

    public static Injector getAnalyisInjector() {
        return Guice.createInjector(new AnalysisInjector());
    }

}
