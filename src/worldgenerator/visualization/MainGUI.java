package worldgenerator.visualization;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.awt.Canvas;
import java.awt.CardLayout;
import javax.swing.JTabbedPane;

import worldgenerator.geometry.civilization.City;
import worldgenerator.geometry.civilization.CityFactory;
import worldgenerator.geometry.civilization.CityFactory.CityAttributes;
import worldgenerator.geometry.forest.ForestFactory.ForestAttributes;
import worldgenerator.geometry.forest.ForestFactory.ForestLevels;
import worldgenerator.geometry.resource.Resources;
import worldgenerator.geometry.terrain.Terrain;
import worldgenerator.geometry.terrain.TerrainFactory;
import worldgenerator.io.GridPlotter2D;
import worldgenerator.locale.Messages;
import worldgenerator.util.grid.Grid2D;
import worldgenerator.util.grid.GridFactory;
import worldgenerator.util.grid.GridFactory.GridAttributes;
import worldgenerator.util.grid.GridType;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.Icon;

public class MainGUI extends JFrame
{

	private JPanel contentPane;
	private ActionListener exitButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			/*int boxresult = IOUtils.chooseYesNoCancel(Messages.getString("SaveBeforeClosing.text"),Messages.getString("SaveBeforeClosing.title"));
			if(boxresult == JOptionPane.YES_OPTION)
			{
				try {
					saveProjectAs(thisFrame.currentProject);
				} catch (IOException e1) {
					IOUtils.errorBox(Messages.getString("SaveFileErrorMessage.text") + System.lineSeparator() + e1.getLocalizedMessage(), Messages.getString("SaveFileErrorMessage.title"));
					e1.printStackTrace();
				}
				System.exit(0);
			}
			if(boxresult == JOptionPane.NO_OPTION)
			{
				System.exit(0);
			}*/
		}
	};

	private Terrain terrain;
	private Collection<City> cities;
	private Grid2D<Integer> cityGrid;
	private Collection<City> villages;
	private Grid2D<Integer> villageGrid;

	private ActionListener generateTerrainActionListener = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			//if(terrain != null)
			{
				//return;
			}
			//else
			{
				// create terrain
				int height = 128;
				int width = 128;
				int seed = 1;// (int)System.currentTimeMillis();
				
				GridAttributes attributes = new GridAttributes(height, width, seed);

				Map<Integer, ForestLevels> forestLevels = new HashMap<Integer, ForestLevels>();
				forestLevels.put(1, new ForestLevels(0.0, 0.5, 0.25));
				
				double forestDensity = 0.1;
				
				ForestAttributes forestAttributes = new ForestAttributes(forestLevels , forestDensity, seed);
				
				terrain = TerrainFactory.create(attributes, forestAttributes);
				
				// create cities
				double cityDensity = 0.002;
				double idealDistance = 5; // distance 5 cols/rows
				int minPop = 1;
				int maxPop = 1000;
				CityAttributes cAttributes = new CityAttributes(cityDensity, minPop, maxPop, idealDistance, seed);
				cities = CityFactory.create(terrain, cAttributes);
				cityGrid = CityFactory.createGrid(cities, attributes);
				
				// create villages
				cityDensity = 0.006;
				idealDistance = 2; // distance 5 cols/rows
				minPop = 1;
				maxPop = maxPop / 3;
				cAttributes = new CityAttributes(cityDensity, minPop, maxPop, idealDistance, seed);
				villages = CityFactory.create(terrain, cAttributes);
				villageGrid = CityFactory.createGrid(villages, attributes);
				
				updateTabs();
			}
		}
	};
	private JLabel heightImageLabel;
	private JLabel resourcesImageLabel;

	private void updateTabs()
	{
		if(terrain == null)
		{
			return;
		}
		
		int width = terrain.getHeightMap().cols();
		int height = terrain.getHeightMap().rows();

		// plot heightmap
		try
		{
			GridPlotter2D plotter = new GridPlotter2D(terrain.getHeightMap());
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			plotter.plot2image(image);
			plotter.plot2file("test_heightmap.txt");
			heightImageLabel.setIcon(new ImageIcon(image));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// plot first resourcemap
		try
		{
			GridPlotter2D plotter = new GridPlotter2D(terrain.getResourceMap(Resources.GOLD));
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			plotter.plot2image(image);
			plotter.plot2file("test_resmap.txt");
			resourcesImageLabel.setIcon(new ImageIcon(image));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// plot watersheds map
		try
		{
			GridPlotter2D plotter = new GridPlotter2D(terrain.getWatershedMap());
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			plotter.plot2image(image);
			plotter.plot2file("test_watershedmap.txt");
			resourcesImageLabel.setIcon(new ImageIcon(image));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// plot soil quality map
		try
		{
			GridPlotter2D plotter = new GridPlotter2D(terrain.getSoilQualityMap());
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			plotter.plot2image(image);
			plotter.plot2file("test_soilmap.txt");
			resourcesImageLabel.setIcon(new ImageIcon(image));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// plot city map
		try
		{
			GridPlotter2D plotter = new GridPlotter2D(cityGrid);
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			plotter.plot2image(image);
			plotter.plot2file("test_citymap.txt");
			resourcesImageLabel.setIcon(new ImageIcon(image));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// plot village map
		try
		{
			GridPlotter2D plotter = new GridPlotter2D(villageGrid);
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			plotter.plot2image(image);
			plotter.plot2file("test_villagemap.txt");
			resourcesImageLabel.setIcon(new ImageIcon(image));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// plot forest map
		try
		{
			int forestType = 1;
			GridPlotter2D plotter = new GridPlotter2D(terrain.getForestMap(forestType));
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			plotter.plot2image(image);
			plotter.plot2file("test_forestmap" + forestType + ".txt");
			resourcesImageLabel.setIcon(new ImageIcon(image));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					MainGUI frame = new MainGUI();
					frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public MainGUI() throws IOException
	{
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
			}
			@Override
			public void componentResized(ComponentEvent e) {
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 679, 458);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("Project");
		menuBar.add(mnFile);
		
		JMenuItem mntmNewProject = new JMenuItem("New Project");
		mnFile.add(mntmNewProject);
		
		JMenuItem mntmLoadProject = new JMenuItem("Load Project...");
		mnFile.add(mntmLoadProject);
		
		JMenuItem mntmSaveProject = new JMenuItem("Save Project");
		mnFile.add(mntmSaveProject);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(exitButtonActionListener );
		mnFile.add(mntmExit);
		
		JMenu mnWorld = new JMenu("World");
		menuBar.add(mnWorld);
		
		JMenuItem mntmGenerate = new JMenuItem(Messages.getString("MainGUI.mntmGenerate.text")); //$NON-NLS-1$
		mntmGenerate.addActionListener(generateTerrainActionListener );
		mnWorld.add(mntmGenerate);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel_heightmap = new JPanel();
		tabbedPane.addTab(Messages.getString("MainGUI.panel_heightmap.title"), null, panel_heightmap, null);
		panel_heightmap.setLayout(null);
		
		int height = 256;
		int width = 256;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		heightImageLabel = new JLabel(new ImageIcon( image ));
		heightImageLabel.setLocation(0, 0);
		heightImageLabel.setSize(256, 256);
		panel_heightmap.add( heightImageLabel );
		
		JPanel panel_resourcemap = new JPanel();
		tabbedPane.addTab(Messages.getString("MainGUI.panel_resourcemap.title"), null, panel_resourcemap, null); //$NON-NLS-1$
		panel_resourcemap.setLayout(null);

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		resourcesImageLabel = new JLabel(new ImageIcon( image ));
		resourcesImageLabel.setBounds(0, 0, 256, 256);
		panel_resourcemap.add(resourcesImageLabel);
	}

}
