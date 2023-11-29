import java.util.ArrayList;
import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    private String data; // transaction data
    private long timeStamp; // as number of milliseconds since 1/1/1970
    private int nonce;
    private int difficulty;
    private int index;
    private String merkleRoot;
    
    public Block(int index, String previousHash, String data) {
        this.index = index;
        this.previousHash = previousHash;
        this.data = data;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }
    
    public String calculateHash() {
        String calculatedHash = StringUtil.applySha256(
                previousHash +
                Long.toString(timeStamp) +
                Integer.toString(nonce) +
                data
        );
        return calculatedHash;
    }
    
    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDificultyString(difficulty); // Create a string with difficulty * "0"
        while(!hash.substring(0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }
}

public class Blockchain {
    private ArrayList<Block> blockchain = new ArrayList<Block>();
    private int difficulty = 5;
    
    public Blockchain() {
        blockchain.add(new Block(0, "0", "Genesis Block"));
    }
    
    public Block getLatestBlock() {
        return blockchain.get(blockchain.size() - 1);
    }
    
    public Block addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
        return newBlock;
    }
    
    public boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        
        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }
}

public class StringUtil {
    public static String applySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String getDificultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }
    
    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<String>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }
        ArrayList<String> treeLayer = previousTreeLayer;
        while(count > 1) {
            treeLayer = new ArrayList<String>();
            for(int i=1; i < previousTreeLayer.size(); i+=2) {
                treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }
}
