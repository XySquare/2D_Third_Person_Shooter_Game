package com.xyy.game.ANN;

import android.util.Log;

/**
 * ��������
 * </br>֧�ֶ�<i>��һ�����ز�</i>������ķ��򴫲�(backprop)����ѵ��
 * 
 * @author Xyy
 *
 */
public class NeuralNet {

	private final double dBias = -1;

	private final double dActivationResponse = 1;

	private final double ERROR_THRESHOLD = 0.003;

	private final int m_iNumInputs;

	private final int m_iNumOutputs;

	private final int m_iNumHiddenLayers;

	private final int m_iNeuronsPerHiddenLyr;

	// backpropѧϰ��
	private final double m_dLearningRate;

	// ����ĺ�ƫ�� (sum (outputs - expected))
	private double m_dErrorSum;

	// true: �����Ѿ���ѵ��
	private Boolean m_bTrained;

	// ʱ��(epoch)������
	private int m_iNumEpochs;

	// ����ÿ����Ԫ�����������
	private NeuronLayer[] m_aryLayers;

    //储存神经网络的输出，优化垃圾回收
    private double[][] outputsContainer;

	/**
	 * �˹������繹�캯��
	 * 
	 * @param NumInputs ��������������
	 * @param NumOutputs �������������
	 * @param NeuronsPerHiddenLyr ���ز�����Ԫ��Ŀ
	 * 
	 */
	public NeuralNet(int NumInputs, int NumOutputs,int NeuronsPerHiddenLyr) {
		m_iNumInputs = NumInputs;
		m_iNumOutputs = NumOutputs;
		m_iNumHiddenLayers = 1;
		m_iNeuronsPerHiddenLyr = NeuronsPerHiddenLyr;
		m_dLearningRate = 0.1;
		m_dErrorSum = 9999;
		m_bTrained = false;
		m_iNumEpochs = 0;
		m_aryLayers = new NeuronLayer[m_iNumHiddenLayers + 1];

		CreateNet();

        int NumLayers = m_iNumHiddenLayers + 1;
        outputsContainer = new double[NumLayers][];
        for (int i = 0; i < NumLayers; ++i) {
            outputsContainer[i] = new double[m_aryLayers[i].m_iNumNeurons];
        }
	}

	/**
	 * 该方法初始化一个神经网络，所以权重为-1~1的随机小数
	 */
	private void CreateNet() {
		// 构建神经网络的每一层
		if (m_iNumHiddenLayers > 0) {
			//构建第一层隐藏层
			m_aryLayers[0] = new NeuronLayer(m_iNeuronsPerHiddenLyr,
					m_iNumInputs);

			//构建余下的隐藏层
			for (int i = 1; i < m_iNumHiddenLayers; ++i) {

				m_aryLayers[i] = new NeuronLayer(m_iNeuronsPerHiddenLyr,
						m_iNeuronsPerHiddenLyr);
			}

			//构建输出层
			m_aryLayers[m_iNumHiddenLayers] = new NeuronLayer(m_iNumOutputs,
					m_iNeuronsPerHiddenLyr);
		}

		else {
			//构建输出层
			m_aryLayers[0] = new NeuronLayer(m_iNumOutputs, m_iNumInputs);
		}
	}

	/**
	 * ��ȡANN��Ȩ��
	 * </br>��ǰ��󣬹�����ȱ�������ȡȨ��
	 * @return Ȩ������
	 */
	public double[] GetWeights() {
		// ����Ȩ��
		double[] weights = new double[GetNumberOfWeights()];
		int curWegiht = 0;

		// ÿһ��
		for (int i = 0; i < m_iNumHiddenLayers + 1; ++i) {

			// ÿ����ԭ
			for (int j = 0; j < m_aryLayers[i].m_iNumNeurons; ++j) {
				// ÿ��Ȩ��
				for (int k = 0; k < m_aryLayers[i].m_aryNeurons[j].m_iNumInputs; ++k) {
					weights[curWegiht] = (m_aryLayers[i].m_aryNeurons[j].m_aryWeight[k]);
					curWegiht++;
				}
			}
		}

		return weights;
	}

	/**
	 * ����ANN��Ȩ������(����Ȩ��+ƫ����)
	 * @return ����Ȩ������
	 */
	public int GetNumberOfWeights() {
		int weights = 0;

		// ÿһ��
		for (int i = 0; i < m_iNumHiddenLayers + 1; ++i) {
			weights += m_aryLayers[i].m_aryNeurons[0].m_iNumInputs
					* m_aryLayers[i].m_iNumNeurons;
		}

		return weights;
	}

	/**
	 * �滻ANN�е�Ȩ��
	 * @param weights Ȩ������
	 */
	public void PutWeights(double[] weights) {
		int cWeight = 0;

		// ÿһ��
		for (int i = 0; i < m_iNumHiddenLayers + 1; ++i) {

			// ÿ����ԭ
			for (int j = 0; j < m_aryLayers[i].m_iNumNeurons; ++j) {
				// ÿ��Ȩ��
				for (int k = 0; k < m_aryLayers[i].m_aryNeurons[j].m_iNumInputs; ++k) {
					m_aryLayers[i].m_aryNeurons[j].m_aryWeight[k] = weights[cWeight];
					cWeight++;
				}
			}
		}
	}

	/**
	 * 该方法通过输入计算输出���
	 * 
	 * @param inputs 网络输入
	 * @return ���������
	 * </br>null-������������ȷ
	 */
	public double[] Update(double[] inputs) {
		//储存每层的输出
		double[] outputs = null;

		int cWeight = 0;

		//检查输入
		if (inputs==null || inputs.length != m_iNumInputs) {
			//如果输入有问题，则返回空（null）
            Log.e("ANN","神经网络输入错误！");
			return null;
		}

		//对每一层...
		for (int i = 0; i < m_iNumHiddenLayers + 1; ++i) {
			if (i > 0) {
				inputs = outputs;
			}

			int NumNeurons = m_aryLayers[i].m_iNumNeurons;

			// outputs.clear();
			//outputs = new double[NumNeurons];
            outputs = outputsContainer[i];

			cWeight = 0;

			//对每个神经元...
			//将输入加权求和
			//通过sigmoid函数过滤得到输出
			for (int j = 0; j < NumNeurons; ++j) {
				double netinput = 0;

				int NumInputs = m_aryLayers[i].m_aryNeurons[j].m_iNumInputs;

				//对每个权重...
				for (int k = 0; k < NumInputs - 1; ++k) {
					//乘以输入并求和
					netinput += m_aryLayers[i].m_aryNeurons[j].m_aryWeight[k]
							* inputs[cWeight++];
				}

				//加入bias
				netinput += m_aryLayers[i].m_aryNeurons[j].m_aryWeight[NumInputs - 1]
						* dBias;

				// ������ֵ��������ǰ��Ԫ
				// ������ֵ����Ҫͨ��S�κ������ˣ����ܵõ����
				m_aryLayers[i].m_aryNeurons[j].m_dActivation = Sigmoid(
						netinput, dActivationResponse);

				// ���浱ǰ��Ԫ�����
				outputs[j] = m_aryLayers[i].m_aryNeurons[j].m_dActivation;

				cWeight = 0;
			}
		}

		return outputs;
	}

	/**
	 * Sigmoid��Ӧ����
	 * 
	 * @param activation ��ϸ������ֵ
	 * @param response ����������״���ϴ�ʱƽ̹����Сʱ����
	 * @return ���˺�ļ���ֵ(0,1)
	 */
	private double Sigmoid(double activation, double response) {
		return (1 / (1 + Math.exp(-activation / response)));
	}

	/**
	 * �Ը�����һѵ�������÷���ִ��backprop�㷨��һ�ε�����
	 * 
	 * @param SetIn ѵ����������������
	 * @param SetOut ���뼯��һϵ���������
	 * @return false: ������������
	 */
	private Boolean NetworkTrainingEpoch(double[][] SetIn, double[][] SetOut) {
		// create some iterators
		double[] aryWeight;
		int curWeight;

		Neuron[] aryNrnHid, aryNrnOut;
		int curNrnHid, curNrnOut;

		// this will hold the cumulative error value for the training set
		m_dErrorSum = 0;

		// ��ÿ������ͨ�����磬���������ݴ˸���Ȩ��
		for (int vec = 0; vec < SetIn.length; ++vec) {
			// ��һ���������������磬��ȡһ�����
			double[] outputs = Update(SetIn[vec]);

			// ���ش���
			if (outputs==null || outputs.length != m_iNumOutputs) {
				return false;
			}

			// ����ÿ�������Ԫ������������Ȩ��
			for (int op = 0; op < m_iNumOutputs; ++op) {
				// �������
				double err = (SetOut[vec][op] - outputs[op]) * outputs[op]
						* (1 - outputs[op]);

				// ��¼���ֵ
				m_aryLayers[1].m_aryNeurons[op].m_dError = err;

				// ����SSE. (����ֵ����Ԥ��ʱ��
				// ���ʾѵ���ɹ�)
				m_dErrorSum += (SetOut[vec][op] - outputs[op])
						* (SetOut[vec][op] - outputs[op]);

				aryWeight = m_aryLayers[1].m_aryNeurons[op].m_aryWeight;
				curWeight = 0;
				aryNrnHid = m_aryLayers[0].m_aryNeurons;
				curNrnHid = 0;

				// ��ÿ��Ȩ�أ���������ƫ����
				while (curWeight != aryWeight.length - 1) {
					// ����backprop���򣬸���Ȩ��
					aryWeight[curWeight] += err * m_dLearningRate
							* aryNrnHid[curNrnHid].m_dActivation;

					++curWeight;
					++curNrnHid;
				}

				// ����ƫ����
				aryWeight[curWeight] += err * m_dLearningRate * dBias;
			}

			// **���������ز�**
			aryNrnHid = m_aryLayers[0].m_aryNeurons;
			curNrnHid = 0;

			int n = 0;

			// ��ÿ�����ز����Ԫ������
			// ���ݴ˵���Ȩ��
			while (curNrnHid != aryNrnHid.length) {
				double err = 0;

				aryNrnOut = m_aryLayers[1].m_aryNeurons;
				curNrnOut = 0;

				// Ϊ�˼������Ԫ�����ֵ����Ҫ�����������ӵ�������������Ԫ��
				// Ȼ��(error * weights)
				while (curNrnOut != aryNrnOut.length) {
					err += aryNrnOut[curNrnOut].m_dError
							* aryNrnOut[curNrnOut].m_aryWeight[n];

					++curNrnOut;
				}

				// ���ڼ������
				err *= aryNrnHid[curNrnHid].m_dActivation
						* (1 - aryNrnHid[curNrnHid].m_dActivation);

				// �Ը���Ԫ��ÿ��Ȩ�أ���������ѧϰ�ʣ�����Ȩ��
				for (int w = 0; w < m_iNumInputs; ++w) {
					// ����backprop���������Ȩ��
					aryNrnHid[curNrnHid].m_aryWeight[w] += err
							* m_dLearningRate * SetIn[vec][w];
				}

				// ����ƫ����
				aryNrnHid[curNrnHid].m_aryWeight[m_iNumInputs] += err
						* m_dLearningRate * dBias;

				++curNrnHid;
				++n;
			}

		}
		// ��һ���������
		return true;
	}

	/**
	 * ��������Ȩ��Ϊ���С��
	 */
	private void InitializeNetwork() {
		// ÿһ��
		for (int i = 0; i < m_iNumHiddenLayers + 1; ++i) {
			// ÿ����Ԫ
			for (int n = 0; n < m_aryLayers[i].m_iNumNeurons; ++n) {
				// ÿ��Ȩ��
				for (int k = 0; k < m_aryLayers[i].m_aryNeurons[n].m_iNumInputs; ++k) {
					m_aryLayers[i].m_aryNeurons[n].m_aryWeight[k] = Math
							.random() * 2 - 1;
				}
			}
		}

		m_dErrorSum = 9999;
		m_iNumEpochs = 0;
	}

	/**
	 * ����һ��ѵ������ѵ������, ֱ��SSEС��Ԥ��ֵ
	 * 
	 * @param data ��ѵ������װ��Data����ʽ����
	 * @return false-������������ݼ����ִ���
	 * 
	 */
	public Boolean Train(Data data) {
		double[][] SetIn = data.GetInputSet();
		double[][] SetOut = data.GetOutputSet();

		// ȷ��ѵ�����Ϸ�
		if ((SetIn.length != SetOut.length)
				|| (SetIn[0].length != m_iNumInputs)
				|| (SetOut[0].length != m_iNumOutputs)) {
			// MessageBox(null, "Inputs != Outputs", "Error", null);

			return false;
		}

		// ��ʼ��Ȩ��Ϊ���С��
		InitializeNetwork();

		// ʹ��backpropѵ��ֱ��SSEС��Ԥ��
		while (m_dErrorSum > ERROR_THRESHOLD) {
			// return false if there are any problems
			if (!NetworkTrainingEpoch(SetIn, SetOut)) {
				return false;
			}

			++m_iNumEpochs;

			// call the render routine to display the error sum
			// InvalidateRect(hwnd, NULL, TRUE);
			// UpdateWindow(hwnd);
		}

		m_bTrained = true;

		return true;
	}

	// ���ʷ���
	/**
	 * ���������Ƿ����ѵ��
	 * @return true: �����ѱ�ѵ�����
	 * </br>false: ������δ��ѵ��������ѵ����
	 */
	public Boolean Trained() {
		return m_bTrained;
	}

	/**
	 * @return ���ص�ǰ����SSE
	 */
	public double Error() {
		return m_dErrorSum;
	}

	/**
	 * @return ��������ѵ������
	 */
	public int Epoch() {
		return m_iNumEpochs;
	}

}
