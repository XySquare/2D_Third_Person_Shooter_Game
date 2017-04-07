package com.xyy.game.ANN;

/**
 * ��ϸ�����࣬���ڴ���һ����Ԫ
 * 
 * @author Xyy
 *
 */
public class NeuronLayer {
	// ����ʹ�õ���ϸ����Ŀ
	public final int m_iNumNeurons;
	
	// ��ϸ���Ĳ�
	public Neuron[] m_aryNeurons;

	public NeuronLayer(int NumNeurons, int NumInputsPerNeuron) {
		m_iNumNeurons = NumNeurons;
		m_aryNeurons = new Neuron[NumNeurons];
		for (int i = 0; i < NumNeurons; i++) {
			m_aryNeurons[i] = new Neuron(NumInputsPerNeuron);
		}
	}
}
