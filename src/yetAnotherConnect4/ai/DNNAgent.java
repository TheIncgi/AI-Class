package yetAnotherConnect4.ai;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
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
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import yetAnotherConnect4.Board;
import yetAnotherConnect4.Player;

public class DNNAgent implements Agent{
	private int HIDDEN_WIDTH = 25;
	MultiLayerNetwork network;
	Logger log = Logger.getLogger(this.getClass());
	
	private static File file;
	private static final File DEFAULT_FILE = new File("DNN-Agent.model");
	
	private DNNAgent(File file) {
		this.file = file;
		load();
		
		network.addListeners(new ScoreIterationListener(1));
	}
	
	public DNNAgent() {
		build();
		file = DEFAULT_FILE;
		
		network.addListeners(new ScoreIterationListener(1));
	}
	
	private void build() {
		int i = 0;
		MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
				.seed(1234)
				.miniBatch(false)
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
				.layer(i++, new RnnOutputLayer.Builder(LossFunction.MCXENT)
						.activation(Activation.SOFTMAX)
						.nIn(HIDDEN_WIDTH)
						.nOut(3)
						.build())
				.backprop(true)
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
			network.feedForward(r.toSingleLiveINDArray(me));
			INDArray input = Nd4j.zeros(9);
			input.putScalar(0, 1); //self..?
			input.putScalar(2+i, 1);
			INDArray out = network.rnnTimeStep(input);
			if(out.getDouble(0) > bestValue) {
				bestValue = out.getDouble(0);
				bestOption = i;
			}
		}
		return bestOption;
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
