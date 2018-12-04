import Koa from "koa";
import _ from "koa-route";
import Blockchain from "./blockchain";

const app = new Koa();
const blockchain = new Blockchain();

const GET = {
  mineBlock: ctx => {
    const previousBlock = blockchain.getPreviousBlock();
    const previousProof = previousBlock.proof;
    const previousHash = blockchain.hash(previousBlock);
    const proof = blockchain.proofOfWork(previousProof);
    const block = blockchain.createBlock({
      proof: proof,
      previousHash: previousHash
    });
    ctx.body = {
      message: "Congraturations, you just mined a block!",
      ...block
    };
  },
  getChain: ctx => {
    ctx.body = {
      chain: blockchain.getChain(),
      length: blockchain.getChain().length
    };
  },
  isChainValid: ctx => {
    ctx.body = {
      is_valid: blockchain.isChainValid(blockchain.getChain())
    };
  }
};

app.use(_.get("/mine_block", GET.mineBlock));
app.use(_.get("/get_chain", GET.getChain));
app.use(_.get("/is_chain_valid", GET.isChainValid));

app.listen(3000);
