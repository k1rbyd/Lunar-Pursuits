const express = require('express');
const mysql = require('mysql2/promise');
const readline = require('readline');
const fs = require('fs/promises');
const path = require('path');
const app = express();
const PORT = 3000;
const bodyParser = require('body-parser');
const http = require('http');

app.use(express.static('public'));
app.use('/views', express.static(__dirname + '/views'));
app.use(bodyParser.urlencoded({ extended: true }));

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

async function promptInput(question) {
  return new Promise((resolve) => {
    rl.question(question, (answer) => {
      resolve(answer.trim());
    });
  });
}

// Define the pool variable at the top level
let pool;

async function configureDatabase() {
  console.log('Configure the database connection:');
  const host = await promptInput('Enter the database host: ');
  const user = await promptInput('Enter the database user: ');
  const password = await promptInput('Enter the database password: ');
  const database = await promptInput('Enter the database name: ');

  return {
    host,
    user,
    password,
    database,
  };
}

// Create an HTTP server using Express
const server = app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});

async function saveDatabaseConfig(dbConfig) {
  const configFile = path.join(__dirname, 'dbconfig.json');
  await fs.writeFile(configFile, JSON.stringify(dbConfig, null, 2));
}

async function loadDatabaseConfig() {
  const configFile = path.join(__dirname, 'dbconfig.json');
  try {
    const data = await fs.readFile(configFile);
    return JSON.parse(data);
  } catch (error) {
    return null;
  }
}

async function start() {
  let dbConfig = await loadDatabaseConfig();

  if (!dbConfig) {
    dbConfig = await configureDatabase();
    await saveDatabaseConfig(dbConfig);
  }

  pool = mysql.createPool(dbConfig); // Initialize the pool here

  // Serve the main menu HTML page
  app.get('/', (req, res) => {
    res.sendFile(__dirname + '/views/main-menu.html');
  });

  app.post('/sell', async (req, res) => {
    const market = req.body.market;
    const productName = req.body.productName;
    const quantity = parseInt(req.body.quantity, 10);
  
    if (!market || !productName || isNaN(quantity) || quantity <= 0) {
      return res.status(400).send('Invalid input. Please provide valid information.');
    }
  
    let connection;
  
    try {
      connection = await pool.getConnection();
      const selectQuery = `SELECT * FROM ${market} WHERE productname = ?`;
      const [rows] = await connection.execute(selectQuery, [productName]);
  
      if (rows.length > 0) {
        // Product already exists in the market; update the quantity
        const updateQuery = `UPDATE ${market} SET quantity = quantity + ? WHERE productname = ?`;
        await connection.execute(updateQuery, [quantity, productName]);
      } else {
        // Product doesn't exist; insert a new record
        const insertQuery = `INSERT INTO ${market} (productname, quantity, price) VALUES (?, ?, 0)`;
        await connection.execute(insertQuery, [productName, quantity]);
      }
  
      const totalPrice = quantity * rows[0].price;
  
      const transactionQuery = `INSERT INTO transactions_from_market (product_name, market_name, quantity, total_price) VALUES (?, ?, ?, ?)`;
      await connection.execute(transactionQuery, [productName, market, quantity, totalPrice]);
  
      connection.release();
  
      console.log(`Selling ${quantity} units of ${productName} in ${market}...`);
      res.redirect('/');
    } catch (error) {
      console.error('Error buying products:', error);
  
      try {
        if (connection) {
          await connection.rollback();
          connection.release();
        }
      } catch (rollbackError) {
        console.error('Error rolling back transaction:', rollbackError);
      }
  
      res.status(500).send('Internal Server Error');
    } finally {
      await mainMenu();
    }
  });

  app.get('/view/stocks', async (req, res) => {
    try {
      const connection = await pool.getConnection();
  
      // Retrieve all products from all markets
      const [rows] = await connection.query('SELECT * FROM market1 UNION ALL SELECT * FROM market2 UNION ALL SELECT * FROM market3');
  
      connection.release();
  
      // Prepare HTML to display the products
      let responseText = '<h2>Stocks for All Products</h2>';
      responseText += '<table>';
      responseText += '<tr><th>Product Name</th><th>Market Name</th><th>Quantity</th><th>Price</th></tr>';
  
      rows.forEach((row) => {
        responseText += `<tr><td>${row.productname}</td><td>${row.market_name}</td><td>${row.quantity}</td><td>${row.price}</td></tr>`;
      });
  
      responseText += '</table>';
  
      res.status(200).send(responseText);
    } catch (error) {
      console.error('Error fetching stocks:', error);
      res.status(500).send('Internal Server Error');
    }
  });

  // Handle the POST request to /exit
  app.post('/exit', (req, res) => {
    console.log('Exiting the application...');
    server.close(() => {
      console.log('Server closed. Exiting...');
      process.exit(0); // Exit the Node.js process with a status code (0 for success)
    });
  });

  app.get('/views/to', async (req, res) => {
    const marketName = req.query.market; // Get the market name from the query parameter
  
    if (!marketName) {
      return res.status(400).send('Market parameter is missing');
    }
  
    try {
      const connection = await pool.getConnection();
      const query = `SELECT * FROM transactions_to_market WHERE market_name = ?`;
      const [rows] = await connection.query(query, [marketName]);
      connection.release();
  
      res.status(200).json(rows);
    } catch (error) {
      console.error('Error fetching transactions to market:', error);
      res.status(500).send('Internal Server Error');
    }
  });
  

  // Handle the POST request when selling products
  app.post('/buy', async (req, res) => {
    const market = req.body.market;
    const productName = req.body.productName;
    const quantity = parseInt(req.body.quantity, 10);
  
    if (!market || !productName || isNaN(quantity) || quantity <= 0) {
      return res.status(400).send('Invalid input. Please provide valid information.');
    }
  
    let connection;
  
    try {
      connection = await pool.getConnection();
      const selectQuery = `SELECT * FROM ${market} WHERE productname = ?`;
  
      // Check if the product exists in the market
      const [rows] = await connection.execute(selectQuery, [productName]);
  
      if (rows.length === 0) {
        return res.status(404).send(`Product ${productName} not found in ${market}.`);
      }
  
      const updateQuery = `UPDATE ${market} SET quantity = quantity - ? WHERE productname = ?`;
  
      const totalPrice = quantity * rows[0].price;
  
      await connection.beginTransaction(); // Start a transaction
  
      // Update the market table
      await connection.execute(updateQuery, [quantity, productName]);
  
      await connection.commit(); // Commit the transaction
  
      connection.release();
  
      console.log(`Selling ${quantity} units of ${productName} in ${market}...`);
      res.redirect('/');
    } catch (error) {
      console.error('Error selling products:', error);
  
      try {
        if (connection) {
          await connection.rollback(); // Rollback the transaction in case of an error
          connection.release();
        }
      } catch (rollbackError) {
        console.error('Error rolling back transaction:', rollbackError);
      }
  
      res.status(500).send('Internal Server Error');
    } finally {
      await mainMenu();
    }
  });

  app.get('/configure', async (req, res) => {
    console.log('Change Database Configuration option selected.');
    let dbConfig = await configureDatabase();
    await saveDatabaseConfig(dbConfig);
    pool = mysql.createPool(dbConfig);
    console.log('Database configuration updated.');
    res.status(200).send('Database configuration updated.');
  });
  
  app.get('/view/from', async (req, res) => {
    const marketName = req.query.market; // Get the market name from the query parameter
  
    if (!marketName) {
      return res.status(400).send('Market parameter is missing');
    }
  
    try {
      const connection = await pool.getConnection();
      const query = `SELECT * FROM transactions_from_market WHERE market_name = ?`;
      const [rows] = await connection.query(query, [marketName]);
      connection.release();
  
      let responseText = `Market: ${marketName}\n\n`;
      responseText += 'Product Name  |  Quantity  |  Total Price\n';
      responseText += '--------------------------------------\n';
  
      rows.forEach((row) => {
        responseText += `${row.product_name}  |  ${row.quantity}  |  $${row.total_price}\n`;
      });
  
      res.status(200).send(responseText);
    } catch (error) {
      console.error('Error fetching transactions from market:', error);
      res.status(500).send('Internal Server Error');
    }
  });
  

  await mainMenu();

  async function mainMenu() {
    console.log('Welcome!');
    console.log('1. Buy Products');
    console.log('2. Sell Products');
    console.log('3. View Transactions From Market');
    console.log('4. Change Database Configuration');
    console.log('5. Exit');

    const option = await promptInput('Select an option (1/2/3/4/5): ');

    switch (option) {
      case '1':
        await buyProducts();
        break;
      case '2':
        await sellProducts();
        break;
      case '3':
        await viewTransactions();
        break;
      case '4':
        dbConfig = await configureDatabase();
        await saveDatabaseConfig(dbConfig);
        pool = mysql.createPool(dbConfig);
        console.log('Database configuration updated.');
        await mainMenu();
        break;
      case '5':
        rl.close();
        break;
      default:
        console.log('Invalid option.');
        await mainMenu();
    }
  }

  async function buyProducts() {
    console.log('Buy Products option selected.');

    const market = await promptInput('Enter the market (market1/market2/market3): ');
    const productName = await promptInput('Enter the product name: ');
    const quantity = parseInt(await promptInput('Enter the quantity: '), 10);

    if (!market || !productName || isNaN(quantity) || quantity <= 0) {
      console.log('Invalid input. Please provide valid information.');
    } else {
      try {
        const connection = await pool.getConnection();
        const insertQuery = `INSERT INTO ${market} (productname, quantity) VALUES (?, ?)`;
        await connection.execute(insertQuery, [productName, quantity]);

        connection.release();

        console.log(`Buying ${quantity} units of ${productName} in ${market}...`);
      } catch (error) {
        console.error('Error buying products:', error);
        console.log('Error buying products. Please try again later.');
      }
    }

    await mainMenu();
  }

  async function sellProducts() {
    console.log('Sell Products option selected.');

    const market = await promptInput('Enter the market (market1/market2/market3): ');
    const productName = await promptInput('Enter the product name: ');
    const quantity = parseInt(await promptInput('Enter the quantity: '), 10);

    if (!market || !productName || isNaN(quantity) || quantity <= 0) {
      console.log('Invalid input. Please provide valid information.');
    } else {
      try {
        const connection = await pool.getConnection();
        const updateQuery = `UPDATE ${market} SET quantity = quantity - ? WHERE productname = ?`;
        await connection.execute(updateQuery, [quantity, productName]);

        connection.release();

        console.log(`Buying ${quantity} units of ${productName} in ${market}...`);
      } catch (error) {
        console.error('Error selling products:', error);
        console.log('Error selling products. Please try again later.');
      }
    }

    await mainMenu();
  }

  async function viewTransactions() {
    console.log('View Transactions option selected.');

    const market = await promptInput('Enter the market (market1/market2/market3): ');

    try {
      const connection = await pool.getConnection();
      const query = `SELECT * FROM ${market}`;
      const [rows] = await connection.execute(query);
      connection.release();

      console.log(`Transactions From ${market}:`);
      console.table(rows);
    } catch (error) {
      console.error('Error fetching transactions:', error);
    } finally {
      await mainMenu();
    }
  }
}

start();
