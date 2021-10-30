import java.awt.Color;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Instrument;

class  Piano extends Thread
{
	JFrame f;
	JLayeredPane j;
	String[] p;
	JButton[] b;
	int[] s;
	int y;
	int o;

	private Synthesizer syn;
	private MidiChannel[] m_ch;
	private Instrument[] inst;
	private int inst_n = 0;

	private JLabel inst_L = new JLabel("instrument: ");
	private JLabel inst_lb = new JLabel("Piano 1");
	private JLabel o_lb = new JLabel("��Ÿ��: 0");

	public Piano(JFrame _f, JLayeredPane _j, String[] _p, JButton[] _b, int[] _s, int _y, int _o){
		f = _f; //frame
		j = _j; //layeredpane
		p = _p; //�ǾƳ� key�迭
		b = _b; //�ǾƳ� ��ư
		s = _s; //Ű���� ������ȣ �迭
		y = _y; //Ű���� ��ġ(���Ʒ� ����)
		o = _o; //��Ÿ�� ���� ���� ����

		/////////////
		// �ǾƳ� Ʋ //
		/////////////
		int temp = 0; // �ǹ� for�� ���� ����
		for (int i=0; i<19; i++){ // �ǹ� ��ư �߰�
			if (i==0 || i%2==0)	{ //��ǹ� �߰�
				j.add(b[i-temp] = new JButton(p[i-temp]), new Integer(0));
				b[i-temp].setVerticalAlignment(SwingConstants.BOTTOM);
				b[i-temp].setBackground(Color.white);
				b[i-temp].setBounds(200+((i/2)*60), 20+y, 60, 250);

			} else if (i%2==1)  { //�����ǹ� �߰�
				j.add(b[i-temp] = new JButton(p[i-temp]), new Integer(100));
				b[i-temp].setBackground(Color.black);
				b[i-temp].setForeground(Color.white);
				b[i-temp].setVerticalAlignment(SwingConstants.BOTTOM);

				if (i==5||i==13){
					temp++;
					continue;
				}else  {
					b[i-temp].setBounds(240+((i/2)*60),20+y,42,160);
				}
			}
		}
	}

	public void run(){
		/////////////
		//   midi  //
		/////////////

		try{	//���� �����ϴ� �ŵ������ ����
			syn = MidiSystem.getSynthesizer();
			syn.open();
		} catch (MidiUnavailableException p) {
			p.printStackTrace();
			System.exit(1);
		}

		this.m_ch = syn.getChannels();
		Soundbank sb = syn.getDefaultSoundbank();
		syn.loadAllInstruments(sb); //���� ��������

		this.inst = syn.getAvailableInstruments();
		syn.loadAllInstruments(syn.getDefaultSoundbank());
		syn.getChannels()[0].programChange(inst_n);

		
		inst_L.setBounds(10,30,70,10);
		inst_lb.setBounds(10,50,150,10);
		o_lb.setBounds(10,70,70,10);

		f.add(inst_L);
		f.add(inst_lb);
		f.add(o_lb);
		f.addKeyListener(new key());
		f.setFocusable(true);
		f.requestFocus();
		f.setSize(1000,600);
		f.setVisible(true);
	}



	/////////////////
	// KeyListener //
	/////////////////
	private int oct = 0;
	class key implements KeyListener{
		public void keyPressed(KeyEvent e){
				int prs = e.getExtendedKeyCode();
				int numbr = -1; //���� ������ �˷��ִ� ����

				for (int j=0; j<17; j++) { //������ �� ȿ���� ���� �߰�
					if (s[j] == prs)	{
						b[j].setBackground(Color.gray);
						numbr = o+j;
					}
				}

				if (prs == KeyEvent.VK_RIGHT)	{ //��������
					if (inst_n == inst.length - 1)	{
						inst_n = 0;
					}else{
						inst_n++;
						inst_lb.setText(inst[inst_n].getName());
					}
					syn.getChannels()[0].programChange(inst_n);
				}else if (prs == KeyEvent.VK_LEFT)	{ //��������
					if (inst_n == 0)	{
						inst_n = inst.length - 1;
					}else{
						inst_n--;
						inst_lb.setText(inst[inst_n].getName());
					}
				}else if (prs == KeyEvent.VK_UP)	{ // �� ��Ÿ�� �ø���
					o += 12;
					oct++;
					o_lb.setText("��Ÿ��: "+oct);
				}else if (prs == KeyEvent.VK_DOWN)	{ // �� ��Ÿ�� ������
					o -= 12;
					oct--;
					o_lb.setText("��Ÿ��: "+oct);
				}


				if (numbr != -1)	{ //���� ���
					m_ch[0].noteOn(numbr, 700);
				}
			}
		public void keyReleased(KeyEvent e){
			//m_ch[0].noteOff(numbr, 700);
			int prs = e.getExtendedKeyCode();
			int numbr = -1; 

			for (int j=0; j<17; j++) {
					if (s[j] == prs)	{
						if (j==1||j==3||j==6||j==8||j==10||j==13||j==15){
							b[j].setBackground(Color.black);
						}else{
							b[j].setBackground(Color.white);
						}
						numbr = o+j;
					}
				}
			if (numbr != -1)	{
				m_ch[0].noteOff(numbr, 700);
			}
		}
		public void keyTyped(KeyEvent e){
		}
	}
}
