package com.myapp.games.towerdefense.model;


class TestLevel extends Level {
    
    private static Wave createTestWave() {
        Wave wave;
        wave = new Wave();
        int factor = 2;
        wave.addElement(3000/factor, new Enemy());
        wave.addElement(1000/factor, new Enemy());
        wave.addElement(3000/factor, new Enemy());
        wave.addElement(1000/factor, new Enemy());
        wave.addElement(1000/factor, new Enemy());
        wave.addElement(3000/factor, new Enemy());
        wave.addElement(1000/factor, new Enemy());
        wave.addElement(1000/factor, new Enemy());
        wave.addElement(1000/factor, new Enemy());
        return wave;
    }

    public TestLevel(int rows, int cols) {
        super(0, 0, rows - 1, cols - 1);

        Wave wave;
        wave = createTestWave();
        super.addElement(30000, wave);
        
        wave = createTestWave();
        super.addElement(30000, wave);

        wave = createTestWave();
        super.addElement(30000, wave);

        wave = createTestWave();
        super.addElement(30000, wave);
    }

    @Override
    public void setUpModel(Grid grid) {
        super.setUpModel(grid);
        grid.getTileAt(0, 3).setWalkable(false);
        grid.getTileAt(1, 3).setWalkable(false);
        grid.getTileAt(2, 3).setWalkable(false);
        grid.getTileAt(2, 6).setWalkable(false);
        grid.getTileAt(2, 7).setWalkable(false);
        grid.getTileAt(2, 8).setWalkable(false);
        grid.setTower(3, 2, new Tower());
        grid.getTileAt(3, 2).setWalkable(false);
        grid.getTileAt(3, 3).setWalkable(false);
        grid.getTileAt(3, 6).setWalkable(false);
        grid.getTileAt(4, 6).setWalkable(false);
        grid.getTileAt(5, 0).setWalkable(false);
        grid.getTileAt(5, 1).setWalkable(false);
        grid.getTileAt(5, 2).setWalkable(false);
        grid.getTileAt(5, 3).setWalkable(false);
        grid.getTileAt(5, 4).setWalkable(false);
        grid.getTileAt(5, 5).setWalkable(false);
        grid.setTower(5, 6, new Tower());
        grid.getTileAt(7, 3).setWalkable(false);  
        grid.getTileAt(7, 4).setWalkable(false);  
        grid.getTileAt(7, 5).setWalkable(false);  
        grid.getTileAt(7, 6).setWalkable(false);  
        grid.getTileAt(7, 7).setWalkable(false);  
        grid.getTileAt(7, 8).setWalkable(false);  
        grid.getTileAt(7, 9).setWalkable(false);  
    }
}
