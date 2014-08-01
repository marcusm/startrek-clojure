/*
 * Super Star Trek
 * Copyright (C) 2008 Michael Birken
 * 
 * This file is part of Super Star Trek.
 *
 * Super Star Trek is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Super Star Trek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SuperStarTrek {


  public class Program {

    public static readonly string[] titleStrings = {
      @"          ______ _______ ______ ______    _______ ______  ______ __  __ ",
      @"         / __  //__  __// __  // __  /   /__  __// __  / / ____// / / /",
      @"        / / /_/   / /  / /_/ // /_/ /      / /  / /_/ / / /__  / // /",
      @"        _\ \     / /  / __  //   __/      / /  /   __/ / __ / /   / ",
      @"      / /_/ /   / /  / / / // /\ \       / /  / /\ \  / /___ / /\ \",
      @"     /_____/   /_/  /_/ /_//_/  \_\     /_/  /_/  \_\/_____//_/  \_\",
      @"",
      @"                      ________________        _",
      @"                      \__(=======/_=_/____.--'-`--.___",
      @"                                 \ \   `,--,-.___.----'",
      @"                               .--`\\--'../",
      @"                              '---._____.|]",
    };

    public static readonly string[] quadrantNames = {
      "Aaamazzara",
      "Altair IV",
      "Aurelia",
      "Bajor",
      "Benthos",
      "Borg Prime",
      "Cait",
      "Cardassia Prime",
      "Cygnia Minor",
      "Daran V",
      "Duronom",
      "Dytallix B",
      "Efros",
      "El-Adrel IV",
      "Epsilon Caneris III",
      "Ferenginar",
      "Finnea Prime",
      "Freehaven",
      "Gagarin IV",
      "Gamma Trianguli VI",
      "Genesis",
      "H'atoria",
      "Holberg 917-G",
      "Hurkos III",
      "Iconia",
      "Ivor Prime",
      "Iyaar",
      "Janus VI",
      "Jouret IV",
      "Juhraya",
      "Kabrel I",
      "Kelva",
      "Ktaris",
      "Ligillium",
      "Loval",
      "Lyshan",
      "Magus III",
      "Matalas",
      "Mudd",
      "Nausicaa",
      "New Bajor",
      "Nova Kron",
      "Ogat",
      "Orion III",
      "Oshionion Prime",
      "Pegos Minor",
      "P'Jem",
      "Praxillus",
      "Qo'noS",
      "Quadra Sigma III",
      "Quazulu VIII",
      "Rakosa V",
      "Rigel VII",
      "Risa",
      "Romulus",
      "Rura Penthe",
      "Sauria",
      "Sigma Draconis",
      "Spica",
      "Talos IV",
      "Tau Alpha C",
      "Ti'Acor",
      "Udala Prime",
      "Ultima Thule",
      "Uxal",
      "Vacca VI",
      "Volan II",
      "Vulcan",
      "Wadi",
      "Wolf 359",
      "Wysanti",
      "Xanthras III",
      "Xendi Sabu",
      "Xindus",
      "Yadalla Prime",
      "Yadera II",
      "Yridian",
      "Zalkon",
      "Zeta Alpha II",
      "Zytchin III",
    };

    private static readonly string[] commandStrings = {
      "--- Commands -----------------",
      "nav = Navigation",
      "srs = Short Range Scan",
      "lrs = Long Range Scan",
      "pha = Phaser Control",
      "tor = Photon Torpedo Control",
      "she = Shield Control",
      "com = Access Computer",
    };

    private static readonly string[] computerStrings = {
      "--- Main Computer --------------",
      "rec = Cumulative Galatic Record",
      "sta = Status Report",
      "tor = Photon Torpedo Calculator",
      "bas = Starbase Calculator",
      "nav = Navigation Calculator",
    };

    private Random random = new Random();
    private int stardate;
    private int timeRemaining;
    private int energy;
    private int klingons;
    private int starbases;
    private int quadrantX, quadrantY;
    private int sectorX, sectorY;
    private int shieldLevel;
    private int navigationDamage;
    private int shortRangeScanDamage;
    private int longRangeScanDamage;
    private int shieldControlDamage;
    private int computerDamage;
    private int photonDamage;
    private int phaserDamage;
    private int photonTorpedoes;
    private bool docked;
    private bool destroyed;
    private int starbaseX, starbaseY;
    private Quadrant[,] quadrants = new Quadrant[8, 8];
    private SectorType[,] sector = new SectorType[8, 8];
    private List<KlingonShip> klingonShips = new List<KlingonShip>();    

    public void Run() {
      PrintStrings(titleStrings);
      while (true) {
        InitializeGame();
        PrintMission();
        GenerateSector();
        while (energy > 0 && !destroyed && klingons > 0 && timeRemaining > 0) {
          CommandPrompt();
          PrintGameStatus();
        }
      }      
    }

    private void PrintGameStatus() {
      if (destroyed) {
        Console.WriteLine("MISSION FAILED: ENTERPRISE DESTROYED!!!");
        Console.WriteLine();
        Console.WriteLine();
        Console.WriteLine();
      } else if (energy == 0) {
        Console.WriteLine("MISSION FAILED: ENTERPRISE RAN OUT OF ENERGY.");
        Console.WriteLine();
        Console.WriteLine();
        Console.WriteLine();
      } else if (klingons == 0) {
        Console.WriteLine("MISSION ACCOMPLISHED: ALL KLINGON SHIPS DESTROYED. WELL DONE!!!");
        Console.WriteLine();
        Console.WriteLine();
        Console.WriteLine();
      } else if (timeRemaining == 0) {
        Console.WriteLine("MISSION FAILED: ENTERPRISE RAN OUT OF TIME.");
        Console.WriteLine();
        Console.WriteLine();
        Console.WriteLine();
      }
    }

    private void CommandPrompt() {
      Console.Write("Enter command: ");
      string command = Console.ReadLine().Trim().ToLower();
      Console.WriteLine();
      switch (command) {
        case "nav":
          Navigation();
          break;
        case "srs":
          ShortRangeScan();
          break;
        case "lrs":
          LongRangeScan();
          break;
        case "pha":
          PhaserControls();
          break;
        case "tor":
          TorpedoControl();
          break;
        case "she":
          ShieldControls();
          break;
        case "com":
          ComputerControls();
          break;
        default:
          PrintStrings(commandStrings);
          break;
      }
    }

    private void ComputerControls() {
      if (computerDamage > 0) {
        Console.WriteLine("The main computer is damaged. Repairs are underway.");
        Console.WriteLine();
        return;
      }

      PrintStrings(computerStrings);
      Console.Write("Enter computer command: ");
      string command = Console.ReadLine().Trim().ToLower();
      switch (command) {
        case "rec":
          DisplayGalaticRecord();
          break;
        case "sta":
          DisplayStatus();
          break;
        case "tor":
          PhotonTorpedoCalculator();
          break;
        case "bas":
          StarbaseCalculator();
          break;
        case "nav":
          NavigationCalculator();
          break;
        default:
          Console.WriteLine();
          Console.WriteLine("Invalid computer command.");
          Console.WriteLine();
          break;
      }
      InduceDamage(4);
    }

    private double ComputeDirection(int x1, int y1, int x2, int y2) {
      double direction = 0;
      if (x1 == x2) {
        if (y1 < y2) {
          direction = 7;
        } else {
          direction = 3;
        }
      } else if (y1 == y2) {
        if (x1 < x2) {
          direction = 1;
        } else {
          direction = 5;
        }
      } else {
        double dy = Math.Abs(y2 - y1);
        double dx = Math.Abs(x2 - x1);
        double angle = Math.Atan2(dy, dx);
        if (x1 < x2) {
          if (y1 < y2) {
            direction = 9.0 - 4.0 * angle / Math.PI;
          } else {
            direction = 1.0 + 4.0 * angle / Math.PI;
          }
        } else {
          if (y1 < y2) {
            direction = 5.0 + 4.0 * angle / Math.PI;
          } else {
            direction = 5.0 - 4.0 * angle / Math.PI;
          }
        }
      }
      return direction;
    }

    private void NavigationCalculator() {
      Console.WriteLine();
      double quadX;
      double quadY;
      Console.WriteLine("Enterprise located in quadrant [{0},{1}].", (quadrantX + 1), (quadrantY + 1));
      Console.WriteLine();
      if (!InputDouble("Enter destination quadrant X (1--8): ", out quadX)
          || quadX < 1 || quadX > 8) {
        Console.WriteLine("Invalid X coordinate.");
        Console.WriteLine();
        return;
      }
      if (!InputDouble("Enter destination quadrant Y (1--8): ", out quadY)
          || quadY < 1 || quadY > 8) {
        Console.WriteLine("Invalid Y coordinate.");
        Console.WriteLine();
        return;
      }
      Console.WriteLine();
      int qx = ((int)(quadX)) - 1;
      int qy = ((int)(quadY)) - 1;
      if (qx == quadrantX && qy == quadrantY) {
        Console.WriteLine("That is the current location of the Enterprise.");
        Console.WriteLine();
        return;
      }
      Console.WriteLine("Direction: {0:#.##}", ComputeDirection(quadrantX, quadrantY, qx, qy));
      Console.WriteLine("Distance:  {0:##.##}", Distance(quadrantX, quadrantY, qx, qy));
      Console.WriteLine();
    }

    private void StarbaseCalculator() {
      Console.WriteLine();
      if (quadrants[quadrantY, quadrantX].starbase) {
        Console.WriteLine("Starbase in sector [{0},{1}].", (starbaseX + 1), (starbaseY + 1));
        Console.WriteLine("Direction: {0:#.##}", ComputeDirection(sectorX, sectorY, starbaseX, starbaseY));
        Console.WriteLine("Distance:  {0:##.##}", Distance(sectorX, sectorY, starbaseX, starbaseY) / 8);
      } else {
        Console.WriteLine("There are no starbases in this quadrant.");
      }
      Console.WriteLine();
    }

    private void PhotonTorpedoCalculator() {
      Console.WriteLine();
      if (klingonShips.Count == 0) {
        Console.WriteLine("There are no Klingon ships in this quadrant.");
        Console.WriteLine();
        return;
      }

      foreach(KlingonShip ship in klingonShips) {
        Console.WriteLine("Direction {2:#.##}: Klingon ship in sector [{0},{1}].",
            (ship.sectorX + 1), (ship.sectorY + 1), 
            ComputeDirection(sectorX, sectorY, ship.sectorX, ship.sectorY));
      }
      Console.WriteLine();
    }

    private void DisplayStatus() {
      Console.WriteLine();
      Console.WriteLine("               Time Remaining: {0}", timeRemaining);
      Console.WriteLine("      Klingon Ships Remaining: {0}", klingons);
      Console.WriteLine("                    Starbases: {0}", starbases);
      Console.WriteLine("           Warp Engine Damage: {0}", navigationDamage);
      Console.WriteLine("   Short Range Scanner Damage: {0}", shortRangeScanDamage);
      Console.WriteLine("    Long Range Scanner Damage: {0}", longRangeScanDamage);
      Console.WriteLine("       Shield Controls Damage: {0}", shieldControlDamage);
      Console.WriteLine("         Main Computer Damage: {0}", computerDamage);
      Console.WriteLine("Photon Torpedo Control Damage: {0}", photonDamage);
      Console.WriteLine("                Phaser Damage: {0}", phaserDamage);
      Console.WriteLine();
    }

    private void DisplayGalaticRecord() {
      Console.WriteLine();
      StringBuilder sb = new StringBuilder();
      Console.WriteLine("-------------------------------------------------");
      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          sb.Append("| ");
          int klingonCount = 0;
          int starbaseCount = 0;
          int starCount = 0;
          Quadrant quadrant = quadrants[i, j];
          if (quadrant.scanned) {
            klingonCount = quadrant.klingons;
            starbaseCount = quadrant.starbase ? 1 : 0;
            starCount = quadrant.stars;
          }
          sb.Append(string.Format("{0}{1}{2} ", klingonCount, starbaseCount, starCount));
        }
        sb.Append("|");
        Console.WriteLine(sb.ToString());
        sb.Length = 0;
        Console.WriteLine("-------------------------------------------------");
      }
      Console.WriteLine();
    }

    private void PhaserControls() {
      if (phaserDamage > 0) {
        Console.WriteLine("Phasers are damaged. Repairs are underway.");
        Console.WriteLine();
        return;
      }

      if (klingonShips.Count == 0) {
        Console.WriteLine("There are no Klingon ships in this quadrant.");
        Console.WriteLine();
        return;
      }

      double phaserEnergy;
      Console.WriteLine("Phasers locked on target.");
      if (!InputDouble(string.Format("Enter phaser energy (1--{0}): ", energy), out phaserEnergy)
          || phaserEnergy < 1 || phaserEnergy > energy) {
        Console.WriteLine("Invalid energy level.");
        Console.WriteLine();
        return;
      }
      Console.WriteLine();

      Console.WriteLine("Firing phasers...");
      List<KlingonShip> destroyedShips = new List<KlingonShip>();
      foreach (KlingonShip ship in klingonShips) {
        energy -= (int)phaserEnergy;
        if (energy < 0) {
          energy = 0;
          break;
        }
        double distance = Distance(sectorX, sectorY, ship.sectorX, ship.sectorY);
        double deliveredEnergy = phaserEnergy * (1.0 - distance / 11.3);
        ship.shieldLevel -= (int)deliveredEnergy;
        if (ship.shieldLevel <= 0) {
          Console.WriteLine("Klingon ship destroyed at sector [{0},{1}].",
              (ship.sectorX + 1), (ship.sectorY + 1));
          destroyedShips.Add(ship);
        } else {
          Console.WriteLine("Hit ship at sector [{0},{1}]. Klingon shield strength dropped to {2}.",
              (ship.sectorX + 1), (ship.sectorY + 1), ship.shieldLevel);   
        }
      }

      foreach (KlingonShip ship in destroyedShips) {
        quadrants[quadrantY, quadrantX].klingons--;
        klingons--;
        sector[ship.sectorY, ship.sectorX] = SectorType.Empty;
        klingonShips.Remove(ship);
      }

      if (klingonShips.Count > 0) {
        Console.WriteLine();
        KlingonsAttack();
      }
      Console.WriteLine();
    }

    private void ShieldControls() {
      Console.WriteLine("--- Shield Controls ----------------");
      Console.WriteLine("add = Add energy to shields.");
      Console.WriteLine("sub = Subtract energy from shields.");
      Console.WriteLine();
      Console.Write("Enter shield control command: ");
      string command = Console.ReadLine().Trim().ToLower();
      Console.WriteLine();
      bool adding = false;
      int maxTransfer = 0;
      if ("add" == command) {
        adding = true;
        maxTransfer = energy;
      } else if ("sub" == command) {
        adding = false;
        maxTransfer = shieldLevel;
      } else {
        Console.WriteLine("Invalid command.");
        Console.WriteLine();
        return;
      }
      double transfer;
      if (!InputDouble(string.Format("Enter amount of energy (1--{0}): ", maxTransfer), out transfer) 
          || transfer < 1 || transfer > maxTransfer) {
        Console.WriteLine("Invalid amount of energy.");
        Console.WriteLine();
        return;
      }
      Console.WriteLine();

      if (adding) {
        energy -= (int)transfer;
        shieldLevel += (int)transfer;        
      } else {
        energy += (int)transfer;
        shieldLevel -= (int)transfer;
      }
      Console.WriteLine("Shield strength is now {0}. Energy level is now {1}.", shieldLevel, energy);
      Console.WriteLine();
    }

    private bool KlingonsAttack() {
      if (klingonShips.Count > 0) {        
        foreach (KlingonShip ship in klingonShips) {
          if (docked) {
            Console.WriteLine("Enterprise hit by ship at sector [{0},{1}]. No damage due to starbase shields.",
                (ship.sectorX + 1), (ship.sectorY + 1));
          } else {
            double distance = Distance(sectorX, sectorY, ship.sectorX, ship.sectorY);
            double deliveredEnergy = 300 * random.NextDouble() * (1.0 - distance / 11.3);
            shieldLevel -= (int)deliveredEnergy;
            if (shieldLevel < 0) {
              shieldLevel = 0;
              destroyed = true;
            }
            Console.WriteLine("Enterprise hit by ship at sector [{0},{1}]. Shields dropped to {2}.",
                (ship.sectorX + 1), (ship.sectorY + 1), shieldLevel);
            if (shieldLevel == 0) {
              return true;
            }
          }
        }
        return true;
      }
      return false;
    }

    private double Distance(double x1, double y1, double x2, double y2) {
      double x = x2 - x1;
      double y = y2 - y1;
      return Math.Sqrt(x * x + y * y);
    }

    private void InduceDamage(int item) {
      if (random.Next(7) > 0) {
        return;
      }

      int damage = 1 + random.Next(5);
      if (item < 0) {
        item = random.Next(7);
      }
      switch (item) {
        case 0:
          navigationDamage = damage;
          Console.WriteLine("Warp engines are malfunctioning.");
          break;
        case 1:
          shortRangeScanDamage = damage;
          Console.WriteLine("Short range scanner is malfunctioning.");
          break;
        case 2:
          longRangeScanDamage = damage;
          Console.WriteLine("Long range scanner is malfunctioning.");
          break;
        case 3:
          shieldControlDamage = damage;
          Console.WriteLine("Shield controls are malfunctioning.");
          break;
        case 4:
          computerDamage = damage;
          Console.WriteLine("The main computer is malfunctioning.");
          break;
        case 5:
          photonDamage = damage;
          Console.WriteLine("Photon torpedo controls are malfunctioning.");
          break;
        case 6:
          phaserDamage = damage;
          Console.WriteLine("Phasers are malfunctioning.");
          break;
      }
      Console.WriteLine();
    }

    private bool RepairDamage() {
      if (navigationDamage > 0) {
        navigationDamage--;
        if (navigationDamage == 0) {
          Console.WriteLine("Warp engines have been repaired.");          
        }
        Console.WriteLine();
        return true;
      }
      if (shortRangeScanDamage > 0) {
        shortRangeScanDamage--;
        if (shortRangeScanDamage == 0) {
          Console.WriteLine("Short range scanner has been repaired.");
        }
        Console.WriteLine();
        return true;
      }
      if (longRangeScanDamage > 0) {
        longRangeScanDamage--;
        if (longRangeScanDamage == 0) {
          Console.WriteLine("Long range scanner has been repaired.");
        }
        Console.WriteLine();
        return true;
      }
      if (shieldControlDamage > 0) {
        shieldControlDamage--;
        if (shieldControlDamage == 0) {
          Console.WriteLine("Shield controls have been repaired.");
        }
        Console.WriteLine();
        return true;
      }
      if (computerDamage > 0) {
        computerDamage--;
        if (computerDamage == 0) {
          Console.WriteLine("The main computer has been repaired.");
        }
        Console.WriteLine();
        return true;
      }
      if (photonDamage > 0) {
        photonDamage--;
        if (photonDamage == 0) {
          Console.WriteLine("Photon torpedo controls have been repaired.");
        }
        Console.WriteLine();
        return true;
      }
      if (phaserDamage > 0) {
        phaserDamage--;
        if (phaserDamage == 0) {
          Console.WriteLine("Phasers have been repaired.");
        }
        Console.WriteLine();
        return true;
      }
      return false;
    }

    private void LongRangeScan() {

      if (longRangeScanDamage > 0) {
        Console.WriteLine("Long range scanner is damaged. Repairs are underway.");
        Console.WriteLine();
        return;
      }

      StringBuilder sb = new StringBuilder();
      Console.WriteLine("-------------------");
      for (int i = quadrantY - 1; i <= quadrantY + 1; i++) {        
        for (int j = quadrantX - 1; j <= quadrantX + 1; j++) {
          sb.Append("| ");
          int klingonCount = 0;
          int starbaseCount = 0;
          int starCount = 0;
          if (i >= 0 && j >= 0 && i < 8 && j < 8) {
            Quadrant quadrant = quadrants[i, j];
            quadrant.scanned = true;
            klingonCount = quadrant.klingons;
            starbaseCount = quadrant.starbase ? 1 : 0;
            starCount = quadrant.stars;
          }
          sb.Append(string.Format("{0}{1}{2} ", klingonCount, starbaseCount, starCount));
        }
        sb.Append("|");
        Console.WriteLine(sb.ToString());
        sb.Length = 0;
        Console.WriteLine("-------------------");
      }      
      Console.WriteLine();
    }

    private void TorpedoControl() {
      if (photonDamage > 0) {
        Console.WriteLine("Photon torpedo control is damaged. Repairs are underway.");
        Console.WriteLine();
        return;
      }

      if (photonTorpedoes == 0) {
        Console.WriteLine("Photon torpedoes exhausted.");
        Console.WriteLine();
        return;
      }

      if (klingonShips.Count == 0) {
        Console.WriteLine("There are no Klingon ships in this quadrant.");
        Console.WriteLine();
        return;
      }

      double direction;
      if (!InputDouble("Enter firing direction (1.0--9.0): ", out direction)
          || direction < 1.0 || direction > 9.0) {
        Console.WriteLine("Invalid direction.");
        Console.WriteLine();
        return;
      }
      Console.WriteLine();
      Console.WriteLine("Photon torpedo fired...");
      photonTorpedoes--;

      double angle = -(Math.PI * (direction - 1.0) / 4.0);
      if (random.Next(3) == 0) {
        angle += ((1.0 - 2.0 * random.NextDouble()) * Math.PI * 2.0) * 0.03;
      }
      double x = sectorX;
      double y = sectorY;
      double vx = Math.Cos(angle) / 20;
      double vy = Math.Sin(angle) / 20;
      int lastX = -1, lastY = -1;
      int newX = sectorX;
      int newY = sectorY;
      while (x >= 0 && y >= 0 && Math.Round(x) < 8 && Math.Round(y) < 8) {
        newX = (int)Math.Round(x);
        newY = (int)Math.Round(y);
        if (lastX != newX || lastY != newY) {
          Console.WriteLine("  [{0},{1}]", newX + 1, newY + 1);
          lastX = newX;
          lastY = newY;
        }
        foreach (KlingonShip ship in klingonShips) {
          if (ship.sectorX == newX && ship.sectorY == newY) {
            Console.WriteLine("Klingon ship destroyed at sector [{0},{1}].",
                (ship.sectorX + 1), (ship.sectorY + 1));
            sector[ship.sectorY, ship.sectorX] = SectorType.Empty;
            klingons--;
            klingonShips.Remove(ship);
            quadrants[quadrantY, quadrantX].klingons--;
            goto label;
          }
        }
        switch (sector[newY, newX]) {
          case SectorType.Starbase:
            starbases--;
            quadrants[quadrantY, quadrantX].starbase = false;
            sector[newY, newX] = SectorType.Empty;
            Console.WriteLine("The Enterprise destroyed a Federation starbase at sector [{0},{1}]!",
                newX + 1, newY + 1);
            goto label;
          case SectorType.Star:
            Console.WriteLine("The torpedo was captured by a star's gravitational field at sector [{0},{1}].",
                newX + 1, newY + 1); 
            goto label;
        }
        x += vx;
        y += vy;
      }
      Console.WriteLine("Photon torpedo failed to hit anything.");

      label:
      
      if (klingonShips.Count > 0) {
        Console.WriteLine();
        KlingonsAttack();
      }
      Console.WriteLine();
    }

    private void Navigation() {

      double maxWarpFactor = 8.0;
      if (navigationDamage > 0) {
        maxWarpFactor = 0.2 + random.Next(9) / 10.0;
        Console.WriteLine("Warp engines damaged. Maximum warp factor: {0}", maxWarpFactor);
        Console.WriteLine();
      }

      double direction, distance;
      if (!InputDouble("Enter course (1.0--9.0): ", out direction)
          || direction < 1.0 || direction > 9.0) {
        Console.WriteLine("Invalid course.");
        Console.WriteLine();
        return;
      }
      if (!InputDouble(string.Format("Enter warp factor (0.1--{0}): ", maxWarpFactor), out distance)
          || distance < 0.1 || distance > maxWarpFactor) {
        Console.WriteLine("Invalid warp factor.");
        Console.WriteLine();
        return;
      }
      Console.WriteLine();

      distance *= 8;
      int energyRequired = (int)distance;
      if (energyRequired >= energy) {
        Console.WriteLine("Unable to comply. Insufficient energy to travel that speed.");
        Console.WriteLine();
        return;
      } else {
        Console.WriteLine("Warp engines engaged.");
        Console.WriteLine();
        energy -= energyRequired;
      }

      int lastQuadX = quadrantX, lastQuadY = quadrantY;
      double angle = -(Math.PI * (direction - 1.0) / 4.0);
      double x = quadrantX * 8 + sectorX;
      double y = quadrantY * 8 + sectorY;
      double dx = distance * Math.Cos(angle);
      double dy = distance * Math.Sin(angle);
      double vx = dx / 1000;
      double vy = dy / 1000;
      int quadX, quadY, sectX, sectY, lastSectX = sectorX, lastSectY = sectorY;
      sector[sectorY, sectorX] = SectorType.Empty;
      for (int i = 0; i < 1000; i++) {
        x += vx;
        y += vy;
        quadX = ((int)Math.Round(x)) / 8;
        quadY = ((int)Math.Round(y)) / 8;
        if (quadX == quadrantX && quadY == quadrantY) {
          sectX = ((int)Math.Round(x)) % 8;
          sectY = ((int)Math.Round(y)) % 8;
          if (sector[sectY, sectX] != SectorType.Empty) {
            sectorX = lastSectX;
            sectorY = lastSectY;
            sector[sectorY, sectorX] = SectorType.Enterprise;
            Console.WriteLine("Encountered obstacle within quadrant.");
            Console.WriteLine();
            goto label;
          }
          lastSectX = sectX;
          lastSectY = sectY;
        }
      }

      if (x < 0) {
        x = 0;
      } else if (x > 63) {
        x = 63;
      }
      if (y < 0) {
        y = 0;
      } else if (y > 63) {
        y = 63;
      }
      quadX = ((int)Math.Round(x)) / 8;
      quadY = ((int)Math.Round(y)) / 8;
      sectorX = ((int)Math.Round(x)) % 8;
      sectorY = ((int)Math.Round(y)) % 8;
      if (quadX != quadrantX || quadY != quadrantY) {
        quadrantX = quadX;
        quadrantY = quadY;
        GenerateSector();        
      } else {
        quadrantX = quadX;
        quadrantY = quadY;        
        sector[sectorY, sectorX] = SectorType.Enterprise;
      }

      label:

      if (IsDockingLocation(sectorY, sectorX)) {
        energy = 3000;
        photonTorpedoes = 10;
        navigationDamage = 0;
        shortRangeScanDamage = 0;
        longRangeScanDamage = 0;
        shieldControlDamage = 0;
        computerDamage = 0;
        photonDamage = 0;
        phaserDamage = 0;
        shieldLevel = 0;
        docked = true;
      } else {
        docked = false;
      }

      if (lastQuadX != quadrantX || lastQuadY != quadrantY) {
        timeRemaining--;
        stardate++;
      }

      ShortRangeScan();

      if (docked) {
        Console.WriteLine("Lowering shields as part of docking sequence...");
        Console.WriteLine("Enterprise successfully docked with starbase.");
        Console.WriteLine();
      } else {
        if (quadrants[quadrantY, quadrantX].klingons > 0
            && lastQuadX == quadrantX && lastQuadY == quadrantY) {
          KlingonsAttack();
          Console.WriteLine();
        } else if (!RepairDamage()) {
          InduceDamage(-1);
        }         
      }
    }

    private bool InputDouble(string prompt, out double value) {
      try {
        Console.Write(prompt);
        value = Double.Parse(Console.ReadLine());
        return true;
      } catch {
        value = 0;
      }
      return false;
    }

    private void GenerateSector() {
      Quadrant quadrant = quadrants[quadrantY, quadrantX];
      bool starbase = quadrant.starbase;
      int stars = quadrant.stars;
      int klingons = quadrant.klingons;
      klingonShips.Clear();
      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          sector[i, j] = SectorType.Empty;
        }
      }
      sector[sectorY, sectorX] = SectorType.Enterprise;
      while (starbase || stars > 0 || klingons > 0) {
        int i = random.Next(8);
        int j = random.Next(8);
        if (IsSectorRegionEmpty(i, j)) {
          if (starbase) {
            starbase = false;
            sector[i, j] = SectorType.Starbase;
            starbaseY = i;
            starbaseX = j;            
          } else if (stars > 0) {
            sector[i, j] = SectorType.Star;
            stars--;
          } else if (klingons > 0) {
            sector[i, j] = SectorType.Kligon;
            KlingonShip klingonShip = new KlingonShip();
            klingonShip.shieldLevel = 300 + random.Next(200);
            klingonShip.sectorY = i;
            klingonShip.sectorX = j;
            klingonShips.Add(klingonShip);
            klingons--;
          }
        }
      }
    }

    private bool IsDockingLocation(int i, int j) {
      for (int y = i - 1; y <= i + 1; y++) {
        for (int x = j - 1; x <= j + 1; x++) {
          if (ReadSector(y, x) == SectorType.Starbase) {
            return true;
          }
        }
      }
      return false;
    }

    private bool IsSectorRegionEmpty(int i, int j) {
      for (int y = i - 1; y <= i + 1; y++) {
        if (ReadSector(y, j - 1) != SectorType.Empty
          && ReadSector(y, j + 1) != SectorType.Empty) {
          return false;
        }
      }
      return ReadSector(i, j) == SectorType.Empty;
    }

    private SectorType ReadSector(int i, int j) {
      if (i < 0 || j < 0 || i > 7 || j > 7) {
        return SectorType.Empty;
      }
      return sector[i, j];
    }

    private void ShortRangeScan() {
      if (shortRangeScanDamage > 0) {
        Console.WriteLine("Short range scanner is damaged. Repairs are underway.");
        Console.WriteLine();
      } else {
        Quadrant quadrant = quadrants[quadrantY, quadrantX];
        quadrant.scanned = true;
        PrintSector(quadrant);        
      }
      Console.WriteLine();
    }

    private void PrintSector(Quadrant quadrant) {
      string condition = "GREEN";
      if (quadrant.klingons > 0) {
        condition = "RED";
      } else if (energy < 300) {        
        condition = "YELLOW";
      }
      StringBuilder sb = new StringBuilder();
      Console.WriteLine("-=--=--=--=--=--=--=--=-             Region: {0}", quadrant.name);            
      PrintSectorRow(sb, 0, string.Format("           Quadrant: [{0},{1}]", quadrantX + 1, quadrantY + 1));
      PrintSectorRow(sb, 1, string.Format("             Sector: [{0},{1}]", sectorX + 1, sectorY + 1));
      PrintSectorRow(sb, 2, string.Format("           Stardate: {0}", stardate));
      PrintSectorRow(sb, 3, string.Format("     Time remaining: {0}", timeRemaining));
      PrintSectorRow(sb, 4, string.Format("          Condition: {0}", condition));
      PrintSectorRow(sb, 5, string.Format("             Energy: {0}", energy));
      PrintSectorRow(sb, 6, string.Format("            Shields: {0}", shieldLevel));
      PrintSectorRow(sb, 7, string.Format("   Photon Torpedoes: {0}", photonTorpedoes));
      Console.WriteLine("-=--=--=--=--=--=--=--=-             Docked: {0}", docked);

      if (quadrant.klingons > 0) {
        Console.WriteLine();
        Console.WriteLine("Condition RED: Klingon ship{0} detected.", (quadrant.klingons == 1 ? "" : "s"));
        if (shieldLevel == 0 && !docked) {
          Console.WriteLine("Warning: Shields are down.");
        }        
      } else if (energy < 300) {
        Console.WriteLine();
        Console.WriteLine("Condition YELLOW: Low energy level.");
        condition = "YELLOW";
      }
    }

    private void PrintSectorRow(StringBuilder sb, int row, string suffix) {      
      for (int column = 0; column < 8; column++) {
        switch (sector[row, column]) {
          case SectorType.Empty:
            sb.Append("   ");
            break;
          case SectorType.Enterprise:
            sb.Append("<*>");
            break;
          case SectorType.Kligon:
            sb.Append("+++");
            break;
          case SectorType.Star:
            sb.Append(" * ");
            break;
          case SectorType.Starbase:
            sb.Append(">!<");
            break;
        }
      }
      if (suffix != null) {
        sb.Append(suffix);
      }
      Console.WriteLine(sb.ToString());
      sb.Length = 0;
    }

    private void PrintMission() {
      Console.WriteLine("Mission: Destroy {0} Klingon ships in {1} stardates with {2} starbases.",
          klingons, timeRemaining, starbases);
      Console.WriteLine();
    }

    private void InitializeGame() {
      quadrantX = random.Next(8);
      quadrantY = random.Next(8);
      sectorX = random.Next(8);
      sectorY = random.Next(8);
      stardate = random.Next(50) + 2250;
      energy = 3000;
      photonTorpedoes = 10;
      timeRemaining = 40 + random.Next(10);
      klingons = 15 + random.Next(6);
      starbases = 2 + random.Next(3);
      destroyed = false;
      navigationDamage = 0;
      shortRangeScanDamage = 0;
      longRangeScanDamage = 0;
      shieldControlDamage = 0;
      computerDamage = 0;
      photonDamage = 0;
      phaserDamage = 0;
      shieldLevel = 0;
      docked = false;

      List<string> names = new List<string>();
      foreach (string name in quadrantNames) {
        names.Add(name);
      }
      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          int index = random.Next(names.Count);
          Quadrant quadrant = new Quadrant();
          quadrants[i, j] = quadrant;
          quadrant.name = names[index];
          quadrant.stars = 1 + random.Next(8);
          names.RemoveAt(index);
        }
      }

      int klingonCount = klingons;
      int starbaseCount = starbases;
      while (klingonCount > 0 || starbaseCount > 0) {
        int i = random.Next(8);
        int j = random.Next(8);
        Quadrant quadrant = quadrants[i, j];
        if (!quadrant.starbase) {
          quadrant.starbase = true;
          starbaseCount--;
        }
        if (quadrant.klingons < 3) {
          quadrant.klingons++;
          klingonCount--;
        }
      }
    }

    private void PrintStrings(string[] strings) {
      foreach (string str in strings) {
        Console.WriteLine(str);
      } 
      Console.WriteLine();
    }

    public static void Main(string[] args) {
      Program program = new Program();
      program.Run();
    }
  }

  class Quadrant {
    public string name;
    public int klingons;
    public int stars;
    public bool starbase;
    public bool scanned;
  }

  enum SectorType { Empty, Star, Kligon, Enterprise, Starbase };

  class KlingonShip {
    public int sectorX;
    public int sectorY;
    public int shieldLevel;
  }
}
