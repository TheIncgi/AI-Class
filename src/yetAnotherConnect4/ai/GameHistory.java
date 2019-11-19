package yetAnotherConnect4.ai;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

public class GameHistory {
	File index, data;

	RandomAccessFile indexRaf, dataRaf;
	//MappedByteBuffer dataMbb;

	private final Object fileLock = new Object();
	int size = 0;

	public GameHistory() {
		try {
		index = new File("gameHistory.index");
		data = new File("gameHistory.data");
		init();
		//dataMbb = dataRaf.getChannel().map(MapMode.READ_WRITE, 0, LENGTH);
		}catch(IOException e) {throw new RuntimeException(e);}
	}

	private void init() throws FileNotFoundException, IOException {
		indexRaf = new RandomAccessFile(index, "rw");
		dataRaf = new RandomAccessFile(data, "rw");

		indexRaf.seek(0);
		if(indexRaf.length()==0) 
			indexRaf.writeInt(size = 0);
		else
			size = indexRaf.readInt();
		System.out.println(Ansi.ansi().fgCyan().a(size).fgBright(Color.WHITE).a(" games loaded"));
	}

	
	private HistoryItterator historyItterator;
	public DataSetIterator getDataSetIterator() {
		if(historyItterator == null)
			historyItterator = new HistoryItterator();
		return historyItterator;

	}

	public void save() {//already on file
	}

	public int getSize() {
		return size;
	}

	public void record(Record currentRecord) {
		try {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		currentRecord.write(baos);
		byte[] data = baos.toByteArray();
		synchronized (fileLock) {
			indexRaf.seek(indexRaf.length());
			dataRaf.seek(dataRaf.length());
			indexRaf.writeLong(dataRaf.getFilePointer());
			indexRaf.writeInt(data.length);
			dataRaf.write(data);
			indexRaf.seek(0);
			indexRaf.writeInt(++size);
		}
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Record get(int recordNumber) throws IOException{
		if(recordNumber<0 || recordNumber > size) return null; byte[] bytes;
		synchronized (fileLock) {
			long index = Integer.BYTES + recordNumber * (Long.BYTES + Integer.BYTES);
			indexRaf.seek(index);
			long filePtr = indexRaf.readLong();
			int length = indexRaf.readInt();
			dataRaf.seek(filePtr);
			bytes = new byte[length];
			dataRaf.read(bytes);
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		return Record.read(bais);
	}

	public class HistoryItterator implements DataSetIterator {
		int pointer = 0;
		@Override
		public boolean hasNext() {
			return pointer < size;
		}

		@Override
		public DataSet next() {
			DataSet dataset;
			try {
				Record record = get(pointer);
				dataset = new DataSet(record.toInputsExampleINDArray(), record.toLabelsINDArray());
			}catch (IOException e) {
				throw new RuntimeException(e);
			}
			if(pointer%(size/100)==0) {
				Instant now = Instant.now();
				Calendar c = Calendar.getInstance();
				System.out.printf("[%2d:%02d:%02d %s] - %6.2f%%\n", c.get(Calendar.HOUR), c.get(Calendar.MINUTE), c.get(Calendar.SECOND), c.get(Calendar.AM_PM)==Calendar.AM?"am":"pm", (100*pointer/(float)size));
			}
			pointer++;
			return dataset;
		}

		@Override
		public DataSet next(int num) {
			return null;
		}

		@Override
		public int totalExamples() {
			return size;
		}

		@Override
		public int inputColumns() {
			throw new RuntimeException("unimplemented");
		}

		@Override
		public int totalOutcomes() {
			throw new RuntimeException("unimplemented");
		}

		@Override
		public boolean resetSupported() {
			return true;
		}

		@Override
		public boolean asyncSupported() {
			return false;
		}

		@Override
		public void reset() {
			pointer = 0;
		}

		@Override
		public int batch() {
			return 1;
		}

		@Override
		public int cursor() {
			return pointer;
		}

		@Override
		public int numExamples() {
			return 2;
		}

		@Override
		public void setPreProcessor(DataSetPreProcessor preProcessor) {
			throw new RuntimeException("unimplemented");
		}

		@Override
		public DataSetPreProcessor getPreProcessor() {
			throw new RuntimeException("unimplemented");
		}

		@Override
		public List<String> getLabels() {
			ArrayList<String> labels = new ArrayList<>();
			Collections.addAll(labels, "win","draw","loose");
			return labels;
		}



	}

	public void wipe() {
		try {
			indexRaf.close();
			index.delete();
			dataRaf.close();
			data.delete();
			
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}
