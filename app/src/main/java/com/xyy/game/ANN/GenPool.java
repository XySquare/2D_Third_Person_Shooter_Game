package com.xyy.game.ANN;

import android.util.Log;

import com.xyy.game.util.MyLog;
import com.xyy.game.util.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * 基因池，
 * 将维护使用两个线性表储存的“基因-适应度”对，
 * 采用“锦标赛竞选法”随机选出适应较高的基因，
 * 并进行随机变异。
 * Created by ${XYY} on ${2016/5/15}.
 * Updated on ${2016/10/16}
 */
public class GenPool {
    /**
     * 该次产生的基因数量
     */
    private int GensCreatedSoFar;
    /**
     * 灾变计数器
     */
    private int cataclysmCounter;
    /**
     * 自上一个最大适应度产生后所经历的代数
     */
    private int generationSinceLastBestFitness;
    /**
     * 储存ANN权重及对应的适应度
     */
    private List<Double> fitnesses;
    private List<double[]> pool;
    /**
     * 在早期，池内没足够基因时，将以此为基础，进行适当变异后返回
     */
    private double[] newGen;

    /**
     * 根据传入的数据初始化基因池
     *
     * @param data 前（length-1）个数据为权重，第length个数据为各个权重对应的适应度
     */
    public GenPool(double[][] data, double[] nativeGen) {
        GensCreatedSoFar = 0;
        cataclysmCounter = 0;
        generationSinceLastBestFitness = 0;
        fitnesses = new ArrayList<>(Params.PopSize + 1);
        pool = new ArrayList<>(Params.PopSize + 1);

        double[] fitness = data[data.length - 1];
        if (fitness.length == data.length + 1) {
            cataclysmCounter = (int) fitness[fitness.length - 2];
            GensCreatedSoFar = (int) fitness[fitness.length - 1];
            Log.i("GenPool", "New Version Data, cataclysmCounter = " + cataclysmCounter + "GensCreatedSoFar = " + GensCreatedSoFar);
        } else if (fitness.length == data.length) {
            cataclysmCounter = (int) fitness[fitness.length - 1];
            Log.i("GenPool", "Old Version Data, cataclysmCounter = " + cataclysmCounter);
        } else if (fitness.length == data.length - 1)
            Log.i("GenPool", "Old Version Data");
        else
            Log.e("GenPool", "Intilization Error!");

        for (int i = 0; i < data.length - 1; i++) {
            pool.add(data[i]);
            fitnesses.add(fitness[i]);
        }

        this.newGen = nativeGen;

        MyLog.i("<!--GENPOOL CREATED INHERIT FROM PREVIOUS SAVED DATA-->");
    }

    public GenPool(double[] newGen) {
        GensCreatedSoFar = 0;
        cataclysmCounter = 0;
        generationSinceLastBestFitness = 0;

        fitnesses = new ArrayList<>(Params.PopSize + 1);
        pool = new ArrayList<>(Params.PopSize + 1);

        this.newGen = newGen;

        MyLog.i("<!--NEW GENPOOL CREATED-->");
    }

    public void inster(double[] weights, float fitness, int liveTime) {
        inster(weights, fitness);
        MyLog.i("<tr>" +
                "<td class=\"generation\">" + GensCreatedSoFar + "</td>" +
                "<td class=\"fitness\">" + fitness + "</td>" +
                "<td class=\"worst\">" + fitnesses.get(fitnesses.size() - 1) + "</td>" +
                "<td class=\"best\">" + fitnesses.get(0) + "</td>" +
                "<td class=\"livetime\">" + liveTime + "</td>" +
                "</tr>");
    }

    private void inster(double[] gen, double fitness) {
        //在适当位置插入当前基因
        int size = fitnesses.size();
        int i;
        for (i = 0; i < size; i++) {
            if (fitness > fitnesses.get(i))
                break;
        }
        fitnesses.add(i, fitness);
        pool.add(i, gen);
        //如果基因达到预设的数量，则移除最差的基因
        if (size >= Params.PopSize) {
            fitnesses.remove(size);
            pool.remove(size);
        }
        //检查当前最高适应度是否更新（当前基因插入位置为0）
        if (i == 0) {
            generationSinceLastBestFitness = 0;
            MyLog.i("<!--NEW BEST FITNESS-->");
        } else
            generationSinceLastBestFitness++;
        ++GensCreatedSoFar;
    }

    public double[] get() {
        //if early in the run then we are still trying out new gens
        if (GensCreatedSoFar < Params.PopSize) {
            Log.i("GenPool", "Try New");
            double[] gen = newGen.clone();
            mutate(gen);
            return gen;
        }
        //otherwise select from the multiset and apply mutation
        else {
            Log.i("GenPool", "Select and Mutation");
            double[] gen = tournamentSelection();

            if (Utils.RandFloat() < 0.8) {
                mutate(gen);
            }
            return gen;
        }
    }

    /**
     * 灾变，
     * 清除基因池中前10%的基因
     *
     * @return true=灾变成功
     */
    public boolean Cataclysm() {
        if (fitnesses.size() < Params.PopSize) {
            //MyLog.i("<!--CATACLYSM FAILED: EARLY-->");
            return false;
        }
        int eliteNum = (int) (Params.PopSize * Params.PercentBestToSelectFrom);
        if (generationSinceLastBestFitness <= eliteNum*2) {
            //MyLog.i("<!--CATACLYSM FAILED: GSLBF = " + generationSinceLastBestFitness+"-->");
            return false;
        }
        cataclysmCounter++;
        generationSinceLastBestFitness = 0;
        MyLog.i("<!--CATACLYSM ACTIVE: CUN = " + cataclysmCounter + " FIT WST/BST = " + fitnesses.get(fitnesses.size() - 1) + " / " + fitnesses.get(0) + "-->");
        pool.subList(0, eliteNum / 4).clear();
        fitnesses.subList(0, eliteNum / 4).clear();
        MyLog.i("<!--CATACLYSM DONE: FIT WST/BST = " + fitnesses.get(fitnesses.size() - 1) + " / " + fitnesses.get(0) + "-->");
        return true;
    }

    /**
     * 获取灾变进度
     * @return 百分比
     */
    public float getCataclysmProcess(){
        float pes1 = (float)fitnesses.size() / Params.PopSize;
        float pes2 = generationSinceLastBestFitness / (Params.PopSize * Params.PercentBestToSelectFrom * 2);
        return pes1*pes2;
    }

    //---------------------------- TournamentSelection -----------------------
    //
    //  performs standard tournament selection given a number of genomes to
    //  sample from each try.
    //------------------------------------------------------------------------
    private double[] tournamentSelection() {
        double BestFitnessSoFar = 0;

        //this will hold the winner of the tournament
        int ChosenGen = 0;

        //Select N members from the population at random testing against
        //the best found so far
        final int size = (fitnesses.size() - 1);
        for (int i = 0; i < Params.NumTourneyCompetitors; ++i) {
            //select an alien from the population at random
            int chose = Utils.RandInt(0, (int) (size * Params.PercentBestToSelectFrom));
            double fitness = fitnesses.get(chose);

            //test it to see if it's fitter than any selected so far
            if (fitness > BestFitnessSoFar) {
                ChosenGen = chose;

                BestFitnessSoFar = fitness;
            }
        }

        //return our champion
        return pool.get(ChosenGen).clone();
    }

    /**
     * 使给定的权重变异
     *
     * @param weights ANN导出的权重
     */
    private static void mutate(double[] weights) {
        final int len = weights.length;
        for (int w = 0; w < len; ++w) {
            //do we perturb this weight?
            if (Utils.RandFloat() < Params.MutationRate) {
                //add a small value to the weight
                weights[w] += (Utils.RandomClamped() * Params.MaxPerturbation);
            }
        }
    }

    /**
     * 获取基因池中的基因及权重，用于保存至本地文件
     *
     * @return 下标为0~size-2的数组为基因，
     * 下标为size-1的数组前size-2位(index range: 0 ~ size-3)为基因所对应的权重,
     * index = size-2 为灾变计数器, index = size-1为代数计数器
     */
    public double[][] getData() {
        int s = pool.size();
        double[][] data = new double[s + 1][];
        pool.toArray(data);

        double[] fitness = new double[s + 2];
        for (int i = 0; i < s; i++) {
            fitness[i] = fitnesses.get(i);
        }
        fitness[s] = cataclysmCounter;
        fitness[s + 1] = GensCreatedSoFar;

        data[s] = fitness;

        return data;
    }

}
