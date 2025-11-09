import express from "express"
import livrosRouter from "./routes/livros.routes";

const app = express();

app.use(express.json());

const PORT = 3001

app.listen(PORT, () => {
    console.log(`Servidor 1 ativo na porta ${PORT}`)
})

app.use('/', livrosRouter);

export default app; 