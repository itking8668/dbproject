#Goal

The data of all blocks is obtained from the blockchain, including the transaction data, and then stored in the local database. This project serves as a bridge between the chain data and the local database.

The blockchain data has the characteristics of only increasing or not modifying. The external database can improve the service performance. Because getting data directly from the chain is slower.

The problem is that data synchronization is sometimes delayed.

#Progress

Currently based on the cita block chain to achieve. And use web3.j tool to get data from block chain. Database based on the sqlite , because it can be easily ported to other projects and need not install. It can be easily replaced with other databases if needed.

Next, based on the ethereum block chain.
