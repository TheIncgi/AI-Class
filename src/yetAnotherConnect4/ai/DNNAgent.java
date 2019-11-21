package yetAnotherConnect4.ai;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JOptionPane;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import yetAnotherConnect4.Board;
import yetAnotherConnect4.Player;
import yetAnotherConnect4.ui.Main;

public class DNNAgent implements Agent{
	private int HIDDEN_WIDTH = 60;
	MultiLayerNetwork network;


	private static File file;
	public static final File DEFAULT_FILE = new File("DNN-Agent.model");

	public DNNAgent() {
		file = DEFAULT_FILE;
		load();
		network.addListeners(new ScoreIterationListener(100));
	}

	private void build() {
		int i = 0;
		MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
				.seed(1234)
				.miniBatch(true)
				.updater(new RmsProp(.001))
				.weightInit(WeightInit.XAVIER).list()

				.layer(i++, new LSTM.Builder()
						.activation(Activation.TANH)
						.nIn(9)
						.nOut(HIDDEN_WIDTH)
						.build())
				.layer(i++, new LSTM.Builder()
						.activation(Activation.TANH)
						.nIn(HIDDEN_WIDTH)
						.nOut(HIDDEN_WIDTH)
						.build())
				.layer(i++, new LSTM.Builder()
						.activation(Activation.TANH)
						.nIn(HIDDEN_WIDTH)
						.nOut(HIDDEN_WIDTH)
						.build())
				.layer(i++, new RnnOutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
						.activation(Activation.SOFTMAX)
						.nIn(HIDDEN_WIDTH)
						.nOut(3)
						.build())
				.backpropType(BackpropType.TruncatedBPTT)
				.tBPTTBackwardLength(6*7+1)
				.build();
		network = new MultiLayerNetwork(config);



		network.init();

	}

	@Override
	public int decide(Board b, Record r, Player me) {
		if(b ==null)throw new NullPointerException("Board null");
		if(r ==null)throw new NullPointerException("Record null");
		if(me==null)throw new NullPointerException("Player null");
		network.rnnClearPreviousState();
		int bestOption =-1;
		double bestValue = Double.NEGATIVE_INFINITY;
		for(int i = 0; i<7; i++) {
			if(!b.canDrop(i)) continue;
			network.rnnTimeStep(r.toSingleLiveINDArray(me));
			INDArray input = Nd4j.zeros(1,9,1);
			input.putScalar(new int[] {0,0, 0}, 1); //self..?
			input.putScalar(new int[] {0, 2+i, 0}, 1);
			INDArray out = network.rnnTimeStep(input);
			double thisValue = out.getDouble(0) - out.getDouble(2); //chance win - chance loose
			if(thisValue > bestValue) {
				bestValue = thisValue;
				bestOption = i;
			}
		}
		return bestOption;
	}

	public INDArray evaluateBoardState(Board b, Record r, Player perspective) {
		if(b ==null)throw new NullPointerException("Board null");
		if(r ==null)throw new NullPointerException("Record null");
		if(perspective==null)throw new NullPointerException("Player perspective null");
		network.rnnClearPreviousState();
		
		return network.rnnTimeStep(r.toSingleLiveINDArray(perspective));
	}

	@Override
	public void train(GameHistory gameHistory) {
		if(gameHistory==null)throw new NullPointerException("No game history provided for training");
		network.fit(gameHistory.getDataSetIterator());
	}

	@Override
	public void save() {
		try {
			ModelSerializer.writeModel(network, file, true);
			System.out.println(Ansi.ansi().fgBrightGreen().a("Netowrk model saved!").reset());
		}catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "IOException has occured while attempting to save the DNN Agent's model");
		}
	}

	private void load() {
		if(file.exists())
			try {
				network = ModelSerializer.restoreMultiLayerNetwork(file);
				System.out.println(Ansi.ansi().fgBrightGreen().a("Netowrk model loaded!").reset());
			}catch (IOException e) {
				e.printStackTrace();
				build();
			}
		else
			build();
	}

	public void evaluate() {
		org.nd4j.evaluation.classification.Evaluation r = network.evaluate(Main.gameHistory.getDataSetIterator());
		System.out.println(Ansi.ansi().bold().fgBrightGreen().a("Evalutation:"));
		printField("  Accuracy: ", r.accuracy());
		printField("  F1: ", r.f1());
		printField("  Precision: ", r.precision());

	}
	public static void printField(String a, Serializable b) {
		System.out.println(Ansi.ansi().boldOff().fgYellow().a(a).bold().fg(Color.WHITE).a(b).reset().toString());
	}

	/*Network:
	 * Deep Q Learning
	 * if next turn wins, this turn has + state for action x
	 * if next turn loses, this turn has - state for action x
	 * if next turn draws, this turn has ~ state for action x
	 * if next turn predicts +, this turn predicts + for action x
	 * ...
	 * Inputs:
	 * self
	 * not self
	 * 1 - if 1/was 1
	 * 2 ...
	 * 3
	 * 4
	 * 5
	 * 6
	 * 7
	 * Outputs:
	 * - state
	 * ~ state
	 * + state
	 * */
}
