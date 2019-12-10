const express = require('express');
const cors = require('cors');
const { db, admin } = require('./utils/admin');



const PORT = process.env.PORT || 8080;

const app = express();

app.use(cors());


app.use(express.json({
    verify: function(req, res, buf, encoding) {
        req.rawBody = buf.toString();
        console.log("rawBody", req.rawBody);
    }
}));

app.get('/', (req, res) => {
    res.send('Hello world\n');
});

const usersRouter = require('./routes/users');
const taskRouter = require('./routes/tasks');
const projectRouter = require('./routes/projects');
const filesyncRouter = require('./routes/filessync')

app.use('/', usersRouter);
app.use('/', taskRouter);
app.use('/', checkAuth, projectRouter);
app.use('/', filesyncRouter);

function checkAuth(req, res, next) {
    console.log("Checking Auth");
    let idToken = req.get("Authorization");

    if(!idToken){
        console.log("Missing token");
        return res.status(403).end();
    }

    admin.auth().verifyIdToken(idToken)
        .then(function(decodedToken) {
            console.log("Successful Auth");
            req.uid = decodedToken.uid;
            next();
        }).catch(function(error) {
            console.log("Failed Auth");
            return res.status(403).end();
        });
}

app.listen(PORT, ()=>{console.log(`Running on http://localhost:${PORT}`);});

module.exports = app;
