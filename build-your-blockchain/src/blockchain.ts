import * as crypto from "crypto";

const DIFFICULTY = "0000";

const sha256HexDigest = (data: any) =>
  crypto
    .createHash("sha256")
    .update(data.toString(), "utf8")
    .digest("hex");

const isHashOpValid = (hashOp: string) => hashOp.startsWith(DIFFICULTY);

const challenge = (proof: number, previousProof: number) =>
  Math.pow(proof, 2) - Math.pow(previousProof, 2);

export default class Blockchain {
  private chain: Block[];

  constructor() {
    this.chain = [];

    // create a genesis block
    this.createBlock({ proof: 1, previousHash: "0" });
  }

  createBlock = (options: { proof: number; previousHash: string }) => {
    const block: Block = {
      index: this.chain.length + 1,
      timestamp: new Date().getTime().toString(),
      proof: options.proof,
      previousHash: options.previousHash
    };
    this.chain.push(block);
    return block;
  };

  getPreviousBlock = () => this.chain[this.chain.length - 1];

  proofOfWork = (previousProof: number) => {
    let newProof = 1;
    let checkProof = false;

    while (!checkProof) {
      const hashOperation = sha256HexDigest(challenge(newProof, previousProof));
      if (isHashOpValid(hashOperation)) {
        checkProof = true;
      } else {
        newProof++;
      }
    }

    return newProof;
  };

  hash = (block: Block) => sha256HexDigest(JSON.stringify(block));

  isChainValid = (chain: Block[]) => {
    let previousBlock = chain[0];
    let blockIndex = 1;
    while (blockIndex < chain.length) {
      const block = chain[blockIndex];
      if (block.previousHash !== this.hash(previousBlock)) {
        return false;
      }
      const hashOperation = sha256HexDigest(
        challenge(block.proof, previousBlock.proof)
      );
      if (!isHashOpValid(hashOperation)) {
        return false;
      }

      previousBlock = block;
      blockIndex++;
    }
    return true;
  };

  getChain = () => this.chain;
}
