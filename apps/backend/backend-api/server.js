const express = require('express');
const { Pool } = require('pg');
const cors = require('cors');

const app = express();
app.use(express.json());
app.use(cors()); 

// Configure seus dados do postgres aqui
const pool = new Pool({
    user: 'postgres',        // Seu usuário do postgres (geralmente é postgres)
    host: 'localhost',
    database: 'sreletric_db', 
    password: '12345678',         
    port: 5432,
});

app.post('/register', async (req, res) => {
    const { email, password, name, birthDate } = req.body;
    try {
        const check = await pool.query("SELECT * FROM usuarios WHERE email = $1", [email]);
        if (check.rows.length > 0) {
            return res.status(400).json({ erro: "Email já cadastrado." });
        }
        
        await pool.query(
            "INSERT INTO usuarios (email, senha_hash, nome, nascimento) VALUES ($1, crypt($2, gen_salt('bf')), $3, $4)",
            [email, password, name, birthDate]
        );
        res.json({ mensagem: "Sucesso" });
    } catch (err) {
        console.error(err);
        res.status(500).json({ erro: "Erro no servidor" });
    }
});

app.post('/login', async (req, res) => {
    const { username, password } = req.body;
    try {
        const result = await pool.query(
            "SELECT * FROM usuarios WHERE email = $1 AND senha_hash = crypt($2, senha_hash)",
            [username, password]
        );

        if (result.rows.length > 0) {
            const user = result.rows[0];
            res.json({ 
                success: true, 
                user: { 
                    name: user.nome, 
                    username: user.email, 
                    birthDate: user.nascimento, 
                    description: "Usuário oficial do sistema." 
                } 
            });
        } else {
            res.status(401).json({ success: false, message: "Senha incorreta." });
        }
    } catch (err) {
        console.error(err);
        res.status(500).json({ success: false, message: "Erro interno." });
    }
});

app.listen(3000, () => console.log('Servidor rodando na porta 3000!'));