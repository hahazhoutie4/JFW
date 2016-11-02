package com.zhoutong.jxl;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.zhoutong.properties.Information;
import com.zhoutong.sql.Persistent;
import jxl.read.biff.BiffException;

public class Test extends JFrame implements ActionListener {
	private static final long serialVersionUID = -5583230886775036859L;
	/**
	 * @author hahazhoutie4
	 * @throws IOException
	 * @throws BiffException
	 * @website cnblogs.com/hahazhoutie4-blogs/
	 */
	private JFileChooser chooser = new JFileChooser();
	private JComboBox<String> jc;
	private String url;
	private static ForWork forwork;
	private JPanel jp2;
	private JPanel jp1;
	private boolean overwrite = true;
	private Persistent p;
	private JButton jb_select;
	private JButton jb_overwrite;
	private static JTextArea ja;
	private Map<String, String> information;
	private OutputExcel outputExcel;
	static {
		ja = new JTextArea();
		ja.setEditable(false);
	}
	private MouseListener mouseListener = new MouseListener() {
		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		/**
		 * @param button_name 选择的构件类型,如：墙、柱、梁、板
		 */
		public void mouseClicked(MouseEvent e) {
			JButton jbutton_click = (JButton) e.getComponent();
			String flag = jbutton_click.getText();
			String button_name = Test.this.jc.getSelectedItem().toString();
			boolean isSpecial = button_name.equals("墙")
					|| button_name.contains("柱") || button_name.contains("梁")
					|| button_name.equals("现浇板") ? true : false;// 是否为特殊构件
			if (flag.equals("确认")) {
				if (null != jb_overwrite) {
					jp1.remove(jb_overwrite);
					jp1.repaint();
					jp1.revalidate();
				}
				Test.this.invoke_name(button_name);
				jp2.removeAll();
				addComponent(jp2, isSpecial);// 此处插入组件
			} else if (flag.equals("覆盖数据？")) {
				overwrite = true;
				p.insertData(forwork, overwrite);
				jp1.remove(jb_overwrite);
				jp1.repaint();
				jp1.revalidate();
				jp2.removeAll();
				addComponent(jp2, isSpecial);// 此处插入组件
			} else if (flag.equals("选择砼强度文件")) {
				File file = new File("*.xls");
				FileNameExtensionFilter f = new FileNameExtensionFilter(null,
						"xls", "xlsx"); // 文件过滤器
				chooser.addChoosableFileFilter(f);
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setCurrentDirectory(new File(
						"C:\\Users\\zhoutong\\Desktop"));
				chooser.showDialog(new JPanel(), "选择");
				File select_file = chooser.getSelectedFile(); // 获取到的文件
				if (null != select_file) {
					ja.setText(select_file.getName());
					information = new Information().getInformation(select_file);
					Map<String, SortedSet<String>> map_information = new Information()
							.getConcrete(information);
					p.insert_other_information(forwork, information); // 此处插入了混凝土强度等级
					Map<String, String> mp_001 = p.getC_1(forwork,
							map_information);
					OutputExcel.getOutputExcel().CreateTable(mp_001,
							"C:\\Users\\zhoutong\\Desktop\\工程量汇总\\1.xls");
					jp2.remove(jb_select);
					jp2.remove(ja);
					jp2.repaint();
					jp2.revalidate();
				} else {
					ja.setText(""); // 未选择任何文件
				}
			}
		}
	};

	public Test() {
		super("广联达提量专用@author:hahazhoutie4");
		this.setBounds(500, 100, 700, 700);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE); // 关闭窗口即关闭进程
	}

	protected void addComponent(JPanel jp, boolean isSpecial) {
		jp.removeAll();
		jp.setLayout(new FlowLayout());
		jp.setVisible(true);
		if (isSpecial) {
			jb_select = new JButton("选择砼强度文件");
			JFileChooser jf = new JFileChooser();
			jb_select.addMouseListener(mouseListener);
			System.out.println("绘制");
			if (null != jb_select) {
				jp.add(jb_select);
			}
			if (null != ja) {
				jp.add(ja);
			}
		}
		jp.repaint();
		jp.revalidate();
		System.out.println("绘制japnel2");
	}

	// 绘制提量的jpanel2
	public void paint_jp2(JPanel jp, ForWork forwork) {
		JTextArea jt = new JTextArea();
		jt.setText("当前选择的工程量:" + forwork.getSheet_name());
		jp.add(jt);
		Information information_001 = new Information();
		Map<String, String> map_information = p.getC_1(forwork,
				information_001.getConcrete(information));
		if (null == outputExcel) {
			outputExcel = OutputExcel.getOutputExcel();
		}
		outputExcel.CreateTable(map_information, forwork.createFileName());
	}

	protected void invoke_name(String sheet_name) {
		forwork.getSheetContent(sheet_name);
		MainInformation mainInformation = forwork.getMainInformation();
		System.out.println(forwork.getNames().length);
		List<String> list = mainInformation.getFloor_information(); // 楼层信息显示在frame中
		if (null == p) {
			p = new Persistent();
		}
		p.createTable(forwork);
		boolean s = p.isDataExist(forwork);
		System.out.println("s的值为"+s);
		if (s) {
			jb_overwrite = new JButton("覆盖数据？");
			jp1.add(jb_overwrite);
			jb_overwrite.addMouseListener(mouseListener);
			jp1.repaint();
			jp1.revalidate();
		} else {
			p.insertData(forwork, s);
		}
		return;
	}

	public void setMainFrame() {
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		JButton jbutton = new JButton("选择文件");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		this.add(jbutton, c);
		jbutton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setCurrentDirectory(new File("I:\\报业主结算书\\2、清单结算\\工程量汇总表格（做完删）"));
		chooser.showDialog(new JLabel(), "选择");
		File file = chooser.getSelectedFile();
		if (null != file) {
			url = file.getPath();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JWindow.getWindows()[0].setVisible(false);
					new Test().setInformationframe(Test.this.invoke(url));
				}
			});
		}
	}

	/**
	 * @param names
	 *            获取到的表单名称
	 */
	protected void setInformationframe(String[] names) {
		JSplitPane js = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		js.setEnabled(false);
		jp1 = new JPanel();
		jp2 = new JPanel();
		this.setLayout(new BorderLayout());
		JTextArea jt = new JTextArea();
		jt.setText("选择工程量:");
		jt.setEditable(false);
		jc = new JComboBox<String>();
		JButton jb = new JButton("确认");
		jb.addMouseListener(mouseListener);
		for (String name : names) {
			jc.addItem(name);
		}
		this.getContentPane().add(js, BorderLayout.CENTER);
		js.add(jp1);
		js.add(jp2);
		jp1.setLayout(new FlowLayout());
		jp1.add(jt);
		jp1.add(jc);
		jp1.add(jc);
		jp1.add(jb);
		this.repaint();
		this.validate();
	}

	protected String[] invoke(String url) {
		forwork = new ForWork();
		try {
			forwork.Initialized(url);
			return forwork.getNames();
		} catch (BiffException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		Test test = new Test();
		test.setMainFrame();
	}
}