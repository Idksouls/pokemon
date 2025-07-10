import java.io.*;
import java.util.*;

import javax.swing.SwingUtilities;

public class Game {
    private List<Player> players;
    private List<Pokemon> wildPokemons;
    private List<Score> topScores;
    private static final String[] STARTER_POKEMONS = {"Bulbasaur", "Charmander", "Squirtle", "Pikachu"};
    private static final String NORMAL_POKEMON_FILE = "normalpokemon.txt";
    private static final String WILD_POKEMON_FILE = "wildpokemon.txt";
    private static final String SCORE_FILE = "score.txt";
    private static final Map<String, Pokemon> STARTER_POKEMON_STATS = new HashMap<>();

    private static final Pokeball[] POKEBALLS = {
        new Pokeball("Poké Ball", 0.5),
        new Pokeball("Great Ball", 0.7),
        new Pokeball("Ultra Ball", 0.9),
        new Pokeball("Master Ball", 1.0)
    };

    // ANSI color codes
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public Game() {
        this.players = new ArrayList<>();
        this.wildPokemons = new ArrayList<>();
        this.topScores = loadScores();
        loadStarterPokemonStats();
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }

    public void start() {
        displayAsciiArt();
        clearWildPokemonFile();
        loadNormalPokemonIntoWild();

        Scanner scanner = new Scanner(System.in);

        String username = "";
        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine().toLowerCase(); // Normalize to lowercase
            if (username.matches("^[a-zA-Z0-9]+$")) {
                break;
            } else {
                System.out.println("Invalid username. Only letters and numbers are allowed.");
            }
        }

        Player player = loadPlayer(username);

        if (player == null) {
            player = new Player();
            player.setName(username);
            player.setPokemons(new ArrayList<>());
            giveStarterPokemon(scanner, player);
            savePlayer(player);
        }

        mainMenu(scanner, player);
    }

    private void displayAsciiArt() {
        System.out.println(ANSI_RED +"  _      __ ____ __   _____ ____   __  ___ ____  "+ ANSI_RESET);
        System.out.println(ANSI_RED +" | | /| / // __// /  / ___// __ \\ /  |/  // __/  "+ ANSI_RESET);
        System.out.println(ANSI_RED +" | |/ |/ // _/ / /__/ /__ / /_/ // /|_/ // _/    "+ ANSI_RESET);
        System.out.println(ANSI_RED +" |__/|__//___//____/\\___/ \\____//_/  /_//___/    "+ ANSI_RESET);
        System.out.println(ANSI_WHITE +" ______ ____                                     "+ ANSI_RESET);
        System.out.println(ANSI_WHITE +"/_  __// __ \\                                    "+ ANSI_RESET);
        System.out.println(ANSI_WHITE +" / /  / /_/ /                                    "+ ANSI_RESET);
        System.out.println(ANSI_WHITE +"/_/   \\____/                                     "+ ANSI_RESET);
        System.out.println(ANSI_RED +"   ___   ____   __ __ ____ __  ___ ____   _  __  "+ ANSI_RESET);
        System.out.println(ANSI_RED +"  / _ \\ / __ \\ / //_// __//  |/  // __ \\ / |/ /  "+ ANSI_RESET);
        System.out.println(ANSI_RED +" / ___// /_/ // ,<  / _/ / /|_/ // /_/ //    /   "+ ANSI_RESET);
        System.out.println(ANSI_RED +"/_/    \\____//_/|_|/___//_/  /_/ \\____//_/|_/    "+ ANSI_RESET);
        System.out.println(ANSI_WHITE +"  _____ ___        ____   __    ____             "+ ANSI_RESET);
        System.out.println(ANSI_WHITE +" / ___// _ | ____ / __ \\ / /   / __/             "+ ANSI_RESET);
        System.out.println(ANSI_WHITE +"/ (_ // __ |/___// /_/ // /__ / _/               "+ ANSI_RESET);
        System.out.println(ANSI_WHITE +"\\___//_/ |_|     \\____//____//___/               "+ ANSI_RESET);
        System.out.println("                                                 ");
    }

    private void clearWildPokemonFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(WILD_POKEMON_FILE))) {
            // Clearing the file by writing nothing.
        } catch (IOException e) {
            System.err.println("Error clearing wild Pokémon file: " + e.getMessage());
        }
    }

    private void loadNormalPokemonIntoWild() {
        List<String> normalPokemonLines = loadPokemonLines(NORMAL_POKEMON_FILE);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(WILD_POKEMON_FILE))) {
            for (String line : normalPokemonLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error loading normal Pokémon into wild Pokémon file: " + e.getMessage());
        }
    }

    private List<String> loadPokemonLines(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading Pokémon lines from file " + filename + ": " + e.getMessage());
        }
        return lines;
    }

    private Player loadPlayer(String username) {
        File file = new File(username.toLowerCase() + "_pokemon.txt"); // Normalize to lowercase
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Player player = new Player();
            player.setName(username);
            List<Pokemon> pokemons = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                pokemons.add(parsePokemon(line));
            }
            player.setPokemons(pokemons);
            return player;
        } catch (IOException e) {
            System.err.println("Error loading player data from file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing player Pokémon data: " + e.getMessage());
        }
        return null;
    }

    private void savePlayer(Player player) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(player.getName().toLowerCase() + "_pokemon.txt"))) { // Normalize to lowercase
            for (Pokemon pokemon : player.getPokemons()) {
                writer.write(formatPokemon(pokemon));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving player data to file: " + e.getMessage());
        }
    }

    private Pokemon parsePokemon(String line) {
        // Example format: "Pikachu,60,60,15,ELECTRIC,ELECTRIC,1"
        String[] parts = line.split(",");
        if (parts.length != 7) {
            throw new IllegalArgumentException("Invalid Pokémon data format: " + line);
        }
        return new Pokemon(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]), PokemonType.valueOf(parts[4]), PokemonType.valueOf(parts[5]), Integer.parseInt(parts[6]));
    }

    private String formatPokemon(Pokemon pokemon) {
        return String.format("%s,%d,%d,%d,%s,%s,%d", pokemon.getName(), pokemon.getHp(), pokemon.getMaxHp(), pokemon.getAttackPower(),
                pokemon.getEffectiveType(), pokemon.getMoveType(), pokemon.getTier());
    }

    private void loadStarterPokemonStats() {
        try (BufferedReader reader = new BufferedReader(new FileReader(NORMAL_POKEMON_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Pokemon pokemon = parsePokemon(line);
                if (Arrays.asList(STARTER_POKEMONS).contains(pokemon.getName())) {
                    STARTER_POKEMON_STATS.put(pokemon.getName(), pokemon);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading starter Pokémon stats: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing starter Pokémon data: " + e.getMessage());
        }
    }

    private void giveStarterPokemon(Scanner scanner, Player player) {
        System.out.println();
        System.out.println("WELCOME " + player.getName() + "!");
        System.out.println("You're given two new starter Pokémon to start off your journey!");
        System.out.println("Choose two starter Pokémon from the following options:");

        for (int i = 0; i < STARTER_POKEMONS.length; i++) {
            System.out.println((i + 1) + ". " + STARTER_POKEMONS[i]);
        }

        List<Pokemon> selectedPokemons = new ArrayList<>();
        List<String> chosenPokemonNames = new ArrayList<>();
        while (selectedPokemons.size() < 2) {
            System.out.print("Choose Pokémon " + (selectedPokemons.size() + 1) + ": ");
            try {
                int choice = scanner.nextInt();
                if (choice > 0 && choice <= STARTER_POKEMONS.length) {
                    String pokemonName = STARTER_POKEMONS[choice - 1];
                    if (chosenPokemonNames.contains(pokemonName)) {
                        System.out.println("You have already selected " + pokemonName + ". Choose a different Pokémon.");
                    } else {
                        selectedPokemons.add(STARTER_POKEMON_STATS.get(pokemonName));
                        chosenPokemonNames.add(pokemonName);
                    }
                } else {
                    System.out.println("Invalid choice. Try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
        player.setPokemons(selectedPokemons);

        for (int i = 0; i < selectedPokemons.size(); i++) {
            Pokemon pokemon = selectedPokemons.get(i);
            String detailsHeader = String.format(" Pokemon Details %d:           ", (i + 1));
            String name = String.format(" Name: %-23s", pokemon.getName());
            String type = String.format(" Type: %-23s", pokemon.getEffectiveType());
            String hp = String.format(" HP: %-24s", pokemon.getHp() + "/" + pokemon.getMaxHp());
            String attackPower = String.format(" Attack Power: %-15s", pokemon.getAttackPower());
            String tier = String.format(" Tier: %-22s", pokemon.getTier());

            System.out.println(ANSI_CYAN + "╔══════════════════════════════╗" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_RED + detailsHeader + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + name + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_RED + type + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + hp + ANSI_RESET + ANSI_CYAN + " ║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_RED + attackPower + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + tier + ANSI_RESET + ANSI_CYAN + " ║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "╚══════════════════════════════╝" + ANSI_RESET);
        }

    }

    private void mainMenu(Scanner scanner, Player player) {
        while (true) {
            System.out.println();
            System.out.println(ANSI_BLUE + "  __  __    _    ___  _  _   __  __  ___  _  _  _   _ ");
            System.out.println(" |  \\/  |  /_\\  |_ _|| \\| | |  \\/  || __|| \\| || | | |");
            System.out.println(" | |\\/| | / _ \\  | | | .` | | |\\/| || _| | .` || |_| |");
            System.out.println(" |_|  |_|/_/ \\_\\|___||_|\\_| |_|  |_||___||_|\\_| \\___/ ");
            System.out.println("                                                      ");
            System.out.println(ANSI_RESET);

            System.out.println(ANSI_CYAN + "╔════════════════════════════════════════╗" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "  1. Catch                              " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_RED + "  2. Battle and Catch                   " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "  3. Pokémon Center (Heal Pokémon)      " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_RED + "  4. View Pokémon Roster                " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "  5. View Top Scores                    " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_RED + "  6. Boss Event                         " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "  7. Save and Exit                      " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "╚════════════════════════════════════════╝" + ANSI_RESET);

            System.out.println(ANSI_CYAN + "╔════════════════════════════════════════╗" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "           Choose an option             " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "╚════════════════════════════════════════╝" + ANSI_RESET);

            System.out.print(ANSI_CYAN + ">>> " + ANSI_RESET);
            try {
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        catchPokemon(scanner, player);
                        break;
                    case 2:
                        battleAndCatch(scanner, player);
                        break;
                    case 3:
                        healPokemons(player);
                        break;
                    case 4:
                        viewPokemonRoster(player);
                        break;
                    case 5:
                        viewTopScores();
                        break;
                    case 6:
                        bossEvent(scanner, player);
                        break;
                    case 7:
                        savePlayer(player);
                        System.out.println("Game saved. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    private void catchPokemon(Scanner scanner, Player player) {
        List<Pokemon> availablePokemons = loadNormalPokemons();
        if (availablePokemons.isEmpty()) {
            System.out.println("No wild Pokémon available to catch.");
            return;
        }

        // Shuffle and pick three random Pokémon
        Collections.shuffle(availablePokemons);
        List<Pokemon> threePokemons = availablePokemons.subList(0, Math.min(3, availablePokemons.size()));

        System.out.println();
        System.out.println(ANSI_CYAN + "╔════════════════════════════════════════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                        Choose one wild Pokémon to catch                        " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "╠════════════════════════════════════════════════════════════════════════════════╣" + ANSI_RESET);
        for (int i = 0; threePokemons != null && i < threePokemons.size(); i++) {
            Pokemon pokemon = threePokemons.get(i);
            String pokemonDetails = String.format(
                ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "%d. %-10s║ HP: %-4d/ %-4d║ Effective Type: %-10s║ Move Type: %-10s" + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET,
                (i + 1), pokemon.getName(), pokemon.getHp(), pokemon.getMaxHp(),
                pokemon.getEffectiveType(), pokemon.getMoveType()
            );
            System.out.println(pokemonDetails);
        }
        System.out.print(ANSI_CYAN + "╚════════════════════════════════════════════════════════════════════════════════╝" + ANSI_RESET + "\n");
        System.out.print(ANSI_CYAN + ">>> " + ANSI_RESET);

        try {
            int choice = scanner.nextInt();
            if (choice > 0 && choice <= threePokemons.size()) {
                Pokemon chosenPokemon = threePokemons.get(choice - 1);
                player.getPokemons().add(chosenPokemon);
                System.out.println("You caught " + chosenPokemon.getName() + "!");
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    private List<Pokemon> loadNormalPokemons() {
        return loadPokemonsFromFile(NORMAL_POKEMON_FILE);
    }

    private List<Pokemon> loadWildPokemons() {
        List<Pokemon> wildPokemons = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(WILD_POKEMON_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wildPokemons.add(parsePokemon(line));
            }
        } catch (IOException e) {
            System.err.println("Error loading wild Pokémon: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing wild Pokémon data: " + e.getMessage());
        }
        return wildPokemons;
    }

    private List<Pokemon> loadHardPokemons() {
        return loadPokemonsFromFile("hardpokemon.txt");
    }

    private List<Pokemon> loadMediumPokemons() {
        return loadPokemonsFromFile("mediumpokemon.txt");
    }

    private List<Pokemon> loadBossPokemons() {
        return loadPokemonsFromFile("bosspokemon.txt");
    }

    private List<Pokemon> loadPokemonsFromFile(String filename) {
        List<Pokemon> pokemons = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                pokemons.add(parsePokemon(line));
            }
        } catch (IOException e) {
            System.err.println("Error loading Pokémon from file " + filename + ": " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing Pokémon data from file " + filename + ": " + e.getMessage());
        }
        return pokemons;
    }

    private double getAttackMultiplier() {
        final BattleGUI gui = new BattleGUI();
        SwingUtilities.invokeLater(() -> {
            gui.setVisible(true);
            gui.toFront();
            gui.requestFocus();
            gui.setAlwaysOnTop(true);
        });

        synchronized (gui) {
            try {
                gui.wait();
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for attack multiplier: " + e.getMessage());
            }
        }

        return gui.getMultiplier();
    }

    private void battleAndCatch(Scanner scanner, Player player) {
        System.out.println();
        System.out.println(ANSI_CYAN + "╔═════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_RED + "           Select difficulty level           " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "╠═════════════════════════════════════════════╣" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "           1. Normal                         " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_RED + "           2. Medium                         " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "           3. Hard                           " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "           4. Main Menu                      " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "╚═════════════════════════════════════════════╝" + ANSI_RESET);

        System.out.println(ANSI_CYAN + "╔═════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "              Enter your choice              " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "╚═════════════════════════════════════════════╝" + ANSI_RESET);

        System.out.print(ANSI_CYAN + ">>> " + ANSI_RESET);
        int difficultyChoice;
        try {
            difficultyChoice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Defaulting to Normal difficulty.");
            scanner.nextLine(); // Clear invalid input
            difficultyChoice = 1;
        }

        List<Pokemon> availablePokemons = new ArrayList<>();

        switch (difficultyChoice) {
            case 1:
                availablePokemons = loadNormalPokemons();
                break;
            case 2:
                availablePokemons = loadMediumPokemons();
                break;
            case 3:
                availablePokemons = loadHardPokemons();
                break;
            case 4:
                mainMenu(scanner, player);
                return; // Exit method to avoid further execution
            default:
                System.out.println("Invalid choice. Defaulting to Normal difficulty.");
                availablePokemons = loadNormalPokemons();
        }

        if (availablePokemons.isEmpty()) {
            System.out.println("No wild Pokémon available for battle.");
            return;
        }

        // Shuffle and pick two random Pokémon for battle
        Collections.shuffle(availablePokemons);
        List<Pokemon> battlePokemons = availablePokemons.subList(0, Math.min(2, availablePokemons.size()));

        System.out.println();
        System.out.println(ANSI_CYAN + "╔════════════════════════════════════════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                                   Battle Mode                                  " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "╠════════════════════════════════════════════════════════════════════════════════╣" + ANSI_RESET);
        for (int i = 0; battlePokemons != null && i < battlePokemons.size(); i++) {
            Pokemon pokemon = battlePokemons.get(i);
            String pokemonDetails = String.format(
                    ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "%d. %-10s" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " HP: %-4d/ %-4d" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Effective Type: %-10s" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Move Type: %-10s" + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET,
                    (i + 1), pokemon.getName(), pokemon.getHp(), pokemon.getMaxHp(),
                    pokemon.getEffectiveType(), pokemon.getMoveType()
            );
            System.out.println(pokemonDetails);
        }
        System.out.print(ANSI_CYAN + "╚════════════════════════════════════════════════════════════════════════════════╝" + ANSI_RESET + "\n");

        List<Pokemon> playerPokemons = player.getPokemons();
        if (playerPokemons.size() < 2) {
            System.out.println("You need at least two Pokémon to battle.");
            return;
        }

        // Filter out Pokémon with HP <= 0
        List<Pokemon> validPlayerPokemons = new ArrayList<>();
        for (Pokemon pokemon : playerPokemons) {
            if (pokemon.getHp() > 0) {
                validPlayerPokemons.add(pokemon);
            }
        }

        if (validPlayerPokemons.size() < 2) {
            System.out.println("You don't have enough healthy Pokémon to battle.");
            return;
        }

        // Allow the player to choose two Pokémon for battle
        List<Pokemon> chosenPokemons = new ArrayList<>();
        System.out.println();
        System.out.println(ANSI_CYAN + "╔═══════════════════════════════════════════════════════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                                      Your Pokémon Roster                                      " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "╠═══════════════════════════════════════════════════════════════════════════════════════════════╣" + ANSI_RESET);

        for (int i = 0; validPlayerPokemons != null && i < validPlayerPokemons.size(); i++) {
            Pokemon pokemon = validPlayerPokemons.get(i);
            String pokemonDetails = String.format(
                    ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "%-2d. %-15s " + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " HP: %-5d/ %-5d " + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Effective Type: %-12s " + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Move Type: %-12s" + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET,
                    (i + 1), pokemon.getName(), pokemon.getHp(), pokemon.getMaxHp(),
                    pokemon.getEffectiveType().name(), pokemon.getMoveType().name()
            );
            System.out.println(pokemonDetails);
        }
        System.out.print(ANSI_CYAN + "╚═══════════════════════════════════════════════════════════════════════════════════════════════╝" + ANSI_RESET + "\n");

        while (chosenPokemons.size() < 2) {
            System.out.println();
            System.out.println(ANSI_CYAN + "╔═════════════════════════════════════════════════════════════════════════════╗" + ANSI_RESET);
            System.out.printf(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                            Choose your Pokémon %d                            " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET + "\n", (chosenPokemons.size() + 1));
            System.out.println(ANSI_CYAN + "╚═════════════════════════════════════════════════════════════════════════════╝" + ANSI_RESET);
            System.out.print(ANSI_CYAN + ">>> " + ANSI_RESET);
            try {
                int choice = scanner.nextInt();
                if (choice > 0 && choice <= validPlayerPokemons.size()) {
                    Pokemon selectedPokemon = validPlayerPokemons.get(choice - 1);
                    if (!chosenPokemons.contains(selectedPokemon)) {
                        chosenPokemons.add(selectedPokemon);
                    } else {
                        System.out.println("You have already selected this Pokémon. Choose a different one.");
                    }
                } else {
                    System.out.println("Invalid choice. Try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        // Display GUI for space bar spamming to get attack multiplier
        double attackMultiplier = getAttackMultiplier();

        // Add space before printing the attack multiplier
        System.out.println();

        System.out.println("\nStarting Battle...\n");

        int playerScore = 0;
        boolean playerWon = battle(scanner, chosenPokemons, battlePokemons, attackMultiplier);

        if (playerWon) {
            System.out.println("\nYou won the battle! You can now attempt to catch the defeated Pokémon.");
            playerScore += calculateScore(chosenPokemons);
            mysteryBallMiniGame(player, battlePokemons);
        } else {
            System.out.println("\nYou lost the battle. Better luck next time.");
        }

        System.out.println(ANSI_CYAN + "╔═════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                  Your Score                 " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "╠═════════════════════════════════════════════╣" + ANSI_RESET);
        System.out.printf(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                    %-4d                    " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET + "\n", playerScore);
        System.out.println(ANSI_CYAN + "╚═════════════════════════════════════════════╝" + ANSI_RESET);

        savePlayer(player);
        saveScore(player.getName(), playerScore);
    }

    private boolean battle(Scanner scanner, List<Pokemon> playerPokemons, List<Pokemon> wildPokemons, double attackMultiplier) {
        Random random = new Random();
        boolean playerTurn = true;
        boolean firstMove = true;

        while (true) {
            if (playerTurn) {
                System.out.println(ANSI_CYAN + "\nYour turn to attack." + ANSI_RESET);
                System.out.println(ANSI_CYAN + "╔════════════════════════════════════════════════════════════════════════════════╗" + ANSI_RESET);
                System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                         Choose your attacking Pokémon                          " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
                System.out.println(ANSI_CYAN + "╠════════════════════════════════════════════════════════════════════════════════╣" + ANSI_RESET);

                for (int i = 0; playerPokemons != null && i < playerPokemons.size(); i++) {
                    Pokemon pokemon = playerPokemons.get(i);
                    String pokemonDetails = String.format(
                        ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "%d. %-10s" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " HP: %-4d/ %-4d" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Effective Type: %-10s" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Move Type: %-10s" + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET,
                        (i + 1), pokemon.getName(), pokemon.getHp(), pokemon.getMaxHp(),
                        pokemon.getEffectiveType(), pokemon.getMoveType()
                    );
                    System.out.println(pokemonDetails);
                }
                System.out.println(ANSI_CYAN + "╚════════════════════════════════════════════════════════════════════════════════╝" + ANSI_RESET + "\n");
                System.out.print(ANSI_CYAN + ">>> " + ANSI_RESET);

                int attackerChoice;
                try {
                    attackerChoice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Clear invalid input
                    continue;
                }

                if (attackerChoice <= 0 || attackerChoice > playerPokemons.size()) {
                    System.out.println(ANSI_RED + "Invalid choice. Try again." + ANSI_RESET);
                    continue;
                }
                Pokemon attacker = playerPokemons.get(attackerChoice - 1);

                System.out.println(ANSI_CYAN + "\nChoose opponent Pokémon to attack:" + ANSI_RESET);
                System.out.println(ANSI_CYAN + "╔════════════════════════════════════════════════════════════════════════════════╗" + ANSI_RESET);
                System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                                   Battle Mode                                  " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
                System.out.println(ANSI_CYAN + "╠════════════════════════════════════════════════════════════════════════════════╣" + ANSI_RESET);

                for (int i = 0; wildPokemons != null && i < wildPokemons.size(); i++) {
                    Pokemon pokemon = wildPokemons.get(i);
                    String pokemonDetails = String.format(
                        ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "%d. %-10s" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " HP: %-4d/ %-4d" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Effective Type: %-10s" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Move Type: %-10s" + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET,
                        (i + 1), pokemon.getName(), pokemon.getHp(), pokemon.getMaxHp(),
                        pokemon.getEffectiveType(), pokemon.getMoveType()
                    );
                    System.out.println(pokemonDetails);
                }
                System.out.println(ANSI_CYAN + "╚════════════════════════════════════════════════════════════════════════════════╝" + ANSI_RESET + "\n");
                System.out.print(ANSI_CYAN + ">>> " + ANSI_RESET);

                int defenderChoice;
                try {
                    defenderChoice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Clear invalid input
                    continue;
                }

                if (defenderChoice <= 0 || defenderChoice > wildPokemons.size()) {
                    System.out.println(ANSI_RED + "Invalid choice. Try again." + ANSI_RESET);
                    continue;
                }
                Pokemon defender = wildPokemons.get(defenderChoice - 1);

                if (defender.getHp() <= 0) {
                    System.out.println(ANSI_RED + defender.getName() + " has fainted and cannot be attacked." + ANSI_RESET);
                    continue;
                }

                // Attack the selected opponent Pokémon
                int damage = calculateDamage(attacker, defender);
                if (firstMove) {
                    damage *= attackMultiplier;
                    firstMove = false;
                }
                defender.setHp(defender.getHp() - damage);
                System.out.println(ANSI_GREEN + attacker.getName() + " attacks " + defender.getName() + " for " + damage + " damage." + ANSI_RESET);
                if (defender.getHp() <= 0) {
                    defender.setHp(0);
                    System.out.println(ANSI_RED + defender.getName() + " is defeated!" + ANSI_RESET);
                    if (allDefeated(wildPokemons)) return true;
                }

                playerTurn = false; // End player's turn
            } else {
                System.out.println(ANSI_CYAN + "\nOpponent's turn to attack." + ANSI_RESET);
                // Randomly select one of the opponent's Pokémon to attack
                Pokemon wildPokemon = wildPokemons.get(random.nextInt(wildPokemons.size()));
                while (wildPokemon.getHp() <= 0) {
                    wildPokemon = wildPokemons.get(random.nextInt(wildPokemons.size()));
                }

                // Randomly select one of the player's Pokémon to be attacked
                Pokemon defender = playerPokemons.get(random.nextInt(playerPokemons.size()));
                while (defender.getHp() <= 0) {
                    defender = playerPokemons.get(random.nextInt(playerPokemons.size()));
                }

                int damage = calculateDamage(wildPokemon, defender);
                defender.setHp(defender.getHp() - damage);
                System.out.println(ANSI_RED + wildPokemon.getName() + " attacks " + defender.getName() + " for " + damage + " damage." + ANSI_RESET);
                if (defender.getHp() <= 0) {
                    defender.setHp(0);
                    System.out.println(ANSI_RED + defender.getName() + " is defeated!" + ANSI_RESET);
                    if (allDefeated(playerPokemons)) return false;
                }

                playerTurn = true; // End opponent's turn
            }
        }
    }

    private int calculateDamage(Pokemon attacker, Pokemon defender) {
        double effectiveness = attacker.getMoveType().getEffectiveAgainst(defender.getEffectiveType());
        return (int) (attacker.getAttackPower() * effectiveness);
    }

    private boolean allDefeated(List<Pokemon> pokemons) {
        for (Pokemon pokemon : pokemons) {
            if (pokemon.getHp() > 0) return false;
        }
        return true;
    }

    private int calculateScore(List<Pokemon> playerPokemons) {
        int score = 0;
        for (Pokemon pokemon : playerPokemons) {
            score += pokemon.getAttackPower() + pokemon.getHp();
        }
        return score;
    }

    private void mysteryBallMiniGame(Player player, List<Pokemon> defeatedPokemons) {
        Random rand = new Random();
        Scanner scanner = new Scanner(System.in);

        System.out.println(ANSI_YELLOW + "✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶");
        System.out.println("✶                                               ✶");
        System.out.println("✶              Congratulations!                 ✶");
        System.out.println("✶     You have defeated the wild Pokémon!       ✶");
        System.out.println("✶                                               ✶");
        System.out.println("✶  Press Enter to receive a Mystery Poké Ball!  ✶");
        System.out.println("✶                                               ✶");
        System.out.println("✶                   ██████╗                     ✶");
        System.out.println("✶                   ╚════██╗                    ✶");
        System.out.println("✶                     ▄███╔╝                    ✶");
        System.out.println("✶                     ▀▀══╝                     ✶");
        System.out.println("✶                     ██╗                       ✶");
        System.out.println("✶                     ╚═╝                       ✶");
        System.out.println("✶                                               ✶");
        System.out.println("✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶✶" + ANSI_RESET);

        scanner.nextLine(); // Wait for the player to press Enter

        Pokeball chosenBall = POKEBALLS[rand.nextInt(POKEBALLS.length)];
        System.out.println(ANSI_CYAN + "\nYou received a " + chosenBall.getType() + "!" + ANSI_RESET + "\n");

        System.out.println(ANSI_CYAN + "╔════════════════════════════════════════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                 Choose a defeated Pokémon to attempt to catch                  " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "╠════════════════════════════════════════════════════════════════════════════════╣" + ANSI_RESET);

        for (int i = 0; defeatedPokemons != null && i < defeatedPokemons.size(); i++) {
            Pokemon pokemon = defeatedPokemons.get(i);
            String pokemonDetails = String.format(
                ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "%d. %-10s" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " HP: %-4d/ %-4d" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Effective Type: %-10s" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Move Type: %-10s" + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET,
                (i + 1), pokemon.getName(), pokemon.getHp(), pokemon.getMaxHp(),
                pokemon.getEffectiveType(), pokemon.getMoveType()
            );
            System.out.println(pokemonDetails);
        }
        System.out.print(ANSI_CYAN + "╚════════════════════════════════════════════════════════════════════════════════╝" + ANSI_RESET + "\n");
        System.out.print(ANSI_CYAN + ">>> " + ANSI_RESET);

        try {
            int choice = scanner.nextInt();
            if (choice > 0 && choice <= defeatedPokemons.size()) {
                Pokemon chosenPokemon = defeatedPokemons.get(choice - 1);
                System.out.println(ANSI_CYAN + "Attempting to catch " + chosenPokemon.getName() + " with the " + chosenBall.getType() + "..." + ANSI_RESET);

                boolean caught = chosenBall.catchPokemon(chosenPokemon);
                if (caught) {
                    player.getPokemons().add(chosenPokemon);
                    System.out.println(ANSI_GREEN + "Congratulations! You caught " + chosenPokemon.getName() + "!" + ANSI_RESET);
                } else {
                    System.out.println(ANSI_RED + "Oh no! " + chosenPokemon.getName() + " escaped!" + ANSI_RESET);
                }
            } else {
                System.out.println(ANSI_RED + "Invalid choice. Try again." + ANSI_RESET);
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    private void saveScore(String playerName, int score) {
        boolean found = false;
        for (Score topScore : topScores) {
            if (topScore.getPlayerName().equalsIgnoreCase(playerName)) { // Compare case-insensitively
                if (score > topScore.getScore()) {
                    topScore.setScore(score);
                }
                found = true;
                break;
            }
        }
        if (!found) {
            topScores.add(new Score(playerName, score));
        }
        Collections.sort(topScores);
        if (topScores.size() > 5) {
            topScores.remove(topScores.size() - 1);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE))) {
            for (Score topScore : topScores) {
                writer.write(topScore.getPlayerName() + "," + topScore.getScore());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage());
        }
    }

    private List<Score> loadScores() {
        List<Score> scores = new ArrayList<>();
        File scoreFile = new File(SCORE_FILE);
        if (!scoreFile.exists()) {
            try {
                scoreFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating score file: " + e.getMessage());
            }
            return scores;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(scoreFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 2) {
                    continue; // Skip this malformed line
                }
                String playerName = parts[0];
                int score;
                try {
                    score = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping line with invalid score: " + line);
                    continue; // Skip this line if the score is not a valid integer
                }
                scores.add(new Score(playerName, score));
            }
        } catch (IOException e) {
            System.err.println("Error loading scores: " + e.getMessage());
        }
        return scores;
    }

    private void viewTopScores() {
        System.out.println();
        System.out.println(ANSI_CYAN + "╔═════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                Top Scores                   " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "╠═════════════════════════════════════════════╣" + ANSI_RESET);

        // Display the top 5 scores
        for (int i = 0; i < Math.min(topScores.size(), 5); i++) {
            Score score = topScores.get(i);
            String scoreDetails = String.format(
                ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "%-2d. %-20s " + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Score: %-10d " + ANSI_CYAN + "║" + ANSI_RESET,
                (i + 1), score.getPlayerName(), score.getScore()
            );
            System.out.println(scoreDetails);
        }
        System.out.print(ANSI_CYAN + "╚═════════════════════════════════════════════╝" + ANSI_RESET + "\n");
    }

    private void viewPokemonRoster(Player player) {
        System.out.println();
        System.out.println(ANSI_CYAN + "╔═══════════════════════════════════════════════════════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                                      Your Pokémon Roster                                      " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "╠═══════════════════════════════════════════════════════════════════════════════════════════════╣" + ANSI_RESET);

        for (int i = 0; i < player.getPokemons().size(); i++) {
            Pokemon pokemon = player.getPokemons().get(i);
            String pokemonDetails = String.format(
                ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "%-2d. %-15s " + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " HP: %-5d/ %-5d " + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Effective Type: %-12s " + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Move Type: %-12s" + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET,
                (i + 1), pokemon.getName(), pokemon.getHp(), pokemon.getMaxHp(),
                pokemon.getEffectiveType().name(), pokemon.getMoveType().name()
            );
            System.out.println(pokemonDetails);
        }
        System.out.print(ANSI_CYAN + "╚═══════════════════════════════════════════════════════════════════════════════════════════════╝" + ANSI_RESET + "\n");
    }

    private void healPokemons(Player player) {
        for (Pokemon pokemon : player.getPokemons()) {
            pokemon.setHp(pokemon.getMaxHp());
        }
        System.out.println("\nAll your Pokémons have been healed!");
    }

    private void bossEvent(Scanner scanner, Player player) {
        List<Pokemon> bossPokemons = loadBossPokemons();
        if (bossPokemons.isEmpty()) {
            System.out.println("No boss Pokémon available.");
            return;
        }

        // Shuffle and pick a random boss Pokémon
        Collections.shuffle(bossPokemons);
        Pokemon bossPokemon = bossPokemons.get(0);

        System.out.println();
        System.out.println(ANSI_CYAN + "╔════════════════════════════════════════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                                    Boss Event                                  " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "╠════════════════════════════════════════════════════════════════════════════════╣" + ANSI_RESET);
        String bossDetails = String.format(
                ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "1. %-10s" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " HP: %-4d/ %-4d" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Effective Type: %-10s" + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Move Type: %-10s" + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET,
                bossPokemon.getName(), bossPokemon.getHp(), bossPokemon.getMaxHp(),
                bossPokemon.getEffectiveType(), bossPokemon.getMoveType()
        );
        System.out.println(bossDetails);
        System.out.print(ANSI_CYAN + "╚════════════════════════════════════════════════════════════════════════════════╝" + ANSI_RESET + "\n");

        List<Pokemon> playerPokemons = player.getPokemons();
        if (playerPokemons.size() < 2) {
            System.out.println("You need at least two Pokémon to battle the boss.");
            return;
        }

        // Filter out Pokémon with HP <= 0
        List<Pokemon> validPlayerPokemons = new ArrayList<>();
        for (Pokemon pokemon : playerPokemons) {
            if (pokemon.getHp() > 0) {
                validPlayerPokemons.add(pokemon);
            }
        }

        if (validPlayerPokemons.size() < 2) {
            System.out.println("You don't have enough healthy Pokémon to battle the boss.");
            return;
        }

        // Allow the player to choose two Pokémon for battle
        List<Pokemon> chosenPokemons = new ArrayList<>();
        System.out.println();
        System.out.println(ANSI_CYAN + "╔═══════════════════════════════════════════════════════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                                      Your Pokémon Roster                                      " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "╠═══════════════════════════════════════════════════════════════════════════════════════════════╣" + ANSI_RESET);

        for (int i = 0; validPlayerPokemons != null && i < validPlayerPokemons.size(); i++) {
            Pokemon pokemon = validPlayerPokemons.get(i);
            String pokemonDetails = String.format(
                    ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "%-2d. %-15s " + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " HP: %-5d/ %-5d " + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Effective Type: %-12s " + ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + " Move Type: %-12s" + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET,
                    (i + 1), pokemon.getName(), pokemon.getHp(), pokemon.getMaxHp(),
                    pokemon.getEffectiveType().name(), pokemon.getMoveType().name()
            );
            System.out.println(pokemonDetails);
        }
        System.out.print(ANSI_CYAN + "╚═══════════════════════════════════════════════════════════════════════════════════════════════╝" + ANSI_RESET + "\n");

        while (chosenPokemons.size() < 2) {
            System.out.println();
            System.out.println(ANSI_CYAN + "╔═════════════════════════════════════════════════════════════════════════════╗" + ANSI_RESET);
            System.out.printf(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                            Choose your Pokémon %d                            " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET + "\n", (chosenPokemons.size() + 1));
            System.out.println(ANSI_CYAN + "╚═════════════════════════════════════════════════════════════════════════════╝" + ANSI_RESET);
            System.out.print(ANSI_CYAN + ">>> " + ANSI_RESET);
            try {
                int choice = scanner.nextInt();
                if (choice > 0 && choice <= validPlayerPokemons.size()) {
                    Pokemon selectedPokemon = validPlayerPokemons.get(choice - 1);
                    if (!chosenPokemons.contains(selectedPokemon)) {
                        chosenPokemons.add(selectedPokemon);
                    } else {
                        System.out.println("You have already selected this Pokémon. Choose a different one.");
                    }
                } else {
                    System.out.println("Invalid choice. Try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        // Display GUI for space bar spamming to get attack multiplier
        double attackMultiplier = getAttackMultiplier();
        System.out.println();

        System.out.println("\nStarting Boss Battle...\n");

        int playerScore = 0;
        boolean playerWon = battle(scanner, chosenPokemons, Collections.singletonList(bossPokemon), attackMultiplier);

        if (playerWon) {
            System.out.println("\nYou won the boss battle! You can now attempt to catch the defeated boss Pokémon.");
            mysteryBallMiniGame(player, Collections.singletonList(bossPokemon));
        } else {
            System.out.println("\nYou lost the boss battle. Better luck next time.");
        }

        System.out.println(ANSI_CYAN + "╔═════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                  Your Score                 " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "╠═════════════════════════════════════════════╣" + ANSI_RESET);
        System.out.printf(ANSI_CYAN + "║" + ANSI_RESET + ANSI_WHITE + "                    %-4d                    " + ANSI_RESET + ANSI_CYAN + "║" + ANSI_RESET + "\n", playerScore);
        System.out.println(ANSI_CYAN + "╚═════════════════════════════════════════════╝" + ANSI_RESET);
        savePlayer(player);
        saveScore(player.getName(), playerScore);
    }

}

