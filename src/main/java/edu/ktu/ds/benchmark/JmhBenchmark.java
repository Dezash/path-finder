package edu.ktu.ds.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import sample.Graph;


// Matuojamas vidutinis testo operacijų (arrayListGet() ir linkedListGet())
// vykdymo laikas. Plačiau žr. JMH pavyzdyje JMHSample_02_BenchmarkModes.java
@BenchmarkMode(Mode.AverageTime)
// Testo klasėje saugoma testo būsena (arrayList, linkedList ir indexes), kuri
// yra bendra visoms testą vykdančioms gijoms. Sudėtingesniems atvejams, kai
// testo metodai keičia testo būseną, t.y. ne tik skaito, bet ir rašo į ją,
// naudojamos atskiros būsenos klasės su įvairiais Scope (Scope.Benchmark arba
// Scope.Thread). Plačiau žr. JMH pavyzdžiuose JMHSample_03_States.java ir
// JMHSample_04_DefaultState.java 
@State(Scope.Benchmark)
// Išmatuotas laikas bus pateiktas mikrosekundėmis. Plačiau žr. JMH pavyzdyje
// JMHSample_02_BenchmarkModes.java 
@OutputTimeUnit(TimeUnit.MICROSECONDS)
// Testo pradžioje atliekamas virtualios mašinos "apšildymas" (@Warmup), po kurio
// testo metodų vykdymo laikas jau matuojamas (@Measurement). Abiem atvejais
// (tiek "apšildymo", tiek matavimų) testas kartojamas keletą kartų, arba
// iteracijų (pagal nutylėjimą atliekamos 5 iteracijos). Kiekvienos iteracijos
// metu testo metodai pakartotinai vykdomi nurodytą laiko tarpą (pvz. 1 sekundę,
// kaip nurodyta time ir timeUnit parametrais). Plačiau žr. JMH pavyzdyje
// JMHSample_20_Annotations.java 
@Warmup(time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(time = 1, timeUnit = TimeUnit.SECONDS)
public class JmhBenchmark {

    static final int OPERATION_COUNT = 10;

    // Parametrai leidžia testą atlikti su skirtingomis konfigūracijomis. Testas
    // bus įvykdytas keletą kartų, kiekvieną sykį listSize užpildant viena iš
    // @Param anotacijoje pateiktų reikšmių. Plačiau žr. JMH pavyzdyje
    // JMHSample_27_Params.java
    @Param({"400", "800", "1600", "3200"})
    public int listSize;

    Graph<Float, TestCost> graph = new Graph<>();
    int[] from = new int[OPERATION_COUNT];
    int[] to = new int[OPERATION_COUNT];
    ArrayList<Float> elements;

    // Testo būsenos valdymo metodai žymimi @Setup (vykdoma prieš testą) arba
    // @TearDown (vykdoma po testo) anotacijomis. Šių metodų vykdymo laikas
    // nematuojamas. Level parametras nurodo, kada metodai bus vykdomi:
    //   Level.Trial - prieš/po visą testą, t.y. testo iteracijų seką
    //   Level.Iteration - prieš/po kiekvieną iteraciją, t.y. testo metodų iškvietimo seką
    //   Level.Invocation - prieš/po kiekvieną testo metodo iškvietimą
    // Plačiau žr. JMH pavyzdžiuose JMHSample_05_StateFixtures.java ir
    // JMHSample_06_FixtureLevel.java
    @Setup(Level.Trial)
    public void generateLists() {
        elements = Util.generateGraph(graph, listSize);
    }

    @Setup(Level.Iteration)
    public void generateIndexes() {
        Util.generateIndexes(from, to, listSize);
    }

    // @Benchmark anotacija žymi metodus, kurių vykdymo laikas yra matuojamas.
    // JMH pagal anotacijas sugeneruoja pagalbinį kodą, leidžianti greitaveikos
    // matavimus atlikti kuo patikimiau. Plačiau žr. JMH pavyzdyje
    // JMHSample_01_HelloWorld.java
    @Benchmark
    public void graphDijkstra(Blackhole bh) {
        for (int i = 0; i < from.length; i++) {
            bh.consume(graph.getPath(elements.get(from[i]), elements.get(to[i])));
        }
    }


    // Rekomenduojamas JMH testų paleidimo būdas, leidžiantis išvengti Java IDE
    // įtakos testo rezultatams - naudoti testo jar failą:
    //   > java -jar target/benchmarks.jar
    // Tačiau laboratorinių darbų metu testų vykdymui patogiau naudoti JMH
    // Runner ir tiesiog įvykdyti testo klasę.
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhBenchmark.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
