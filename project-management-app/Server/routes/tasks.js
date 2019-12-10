const router = require('express').Router();
const { admin, db } = require('../utils/admin');

let randomDate = (from, to) => {
    from = from.getTime();
    to = to.getTime();
    return new Date(from + Math.random() * (to - from));
};

router.route("/generateTask").get((req, res) => {

    db.collection('projects').get().then(resp => {

        let generatedTasks = [];

        let projects = resp.docs.map(queryDocumentSnapshot => {
            return ({
                id: queryDocumentSnapshot.id
            })

        })
        let projectsN = projects.length;

        for (let i = 0; i < projectsN; ++i) {
            let project = projects[i];
            let task = {
                projectID: project.id,
                description: "task" + "Personal" + i + "Description",
                status: 'pending',
                creationDate: new Date().toISOString(),
                deadline: randomDate(new Date("2019-12-15"), new Date("2020-03-25")).toISOString()
            };

            let task2 = {
                projectID: project.id,
                description: "Project" + "Personal" + (i + 10) + "Description2",
                creationDate: new Date().toISOString(),
                status: 'pending',
                deadline: randomDate(new Date("2019-12-15"), new Date("2020-03-25")).toISOString()
            };

            generatedTasks.push(task);
            generatedTasks.push(task2);

        }

        let batch = db.batch();

        generatedTasks.forEach((tasks) => {
            let ref = db.collection("tasks").doc();
            batch.set(ref, tasks)
        });

        batch.commit().then(documentReference => {
            return res.status(201).json({ message: "create tasks success" })
        })
            .catch(err => {
                return res.status(400).json({ err: err, message: err.message })
            });

    })
        .catch(err => {
            return res.status(400).json({ err: err.message })
        })
});

router.route("/generateTaskId").get((req, res) => {
    db.collection('tasks').get().then(resp => {
        let tasksIds = [];

        let tasks = resp.docs.map(queryDocumentSnapshot => {
            return ({
                    id: queryDocumentSnapshot.id,
                })

        })
        let tasksN = tasks.length;

        tasks.forEach(item =>{
            tasksIds.push(item);
        })

        let batch = db.batch();

        tasksIds.forEach((taskIds) => {
            let ref = db.collection("tasks").doc(taskIds.id);
            batch.update(ref, taskIds)
        });

        batch.commit().then(documentReference => {
            return res.status(201).json({ message: "update tasksid success" })
        })
            .catch(err => {
                return res.status(400).json({ err: err, message: err.message })
            });

    })
        .catch(err => {
            return res.status(400).json({ err: err.message })
        })
})

router.route('/task').post((req, res) => {
    const creationDate = new Date();
    let task = {
        projectID: req.body.projectID,
        status: req.body.status,
        description: req.body.description,
        deadline: req.body.deadline,
        creationDate: creationDate.toISOString()
    };

    db.collection("tasks").add(task)
        .then(taskRes => {
            console.log(taskRes.id)
            let task = db.collection("tasks").doc(taskRes.id)
            return (
                task.update({
                    id: taskRes.id
                })
            )
        })
        .then(() => {
            return res.status(201).json({ message: "create task success" })
        })
        .catch(err => {
            return res.status(400).json("Error" + err)
        })
});

//query a list of tasks of a project
router.route('/tasks/:projectID').get((req, res) => {
    db.collection("tasks").where('projectID', '==', req.params.projectID).get()
        .then(resp => {
            if (resp.empty) {
                return res.json({ message: "No matching tasks." })
            }
            else {
                let tasks = []
                resp.forEach(task => {
                    tasks.push(task.data())
                });
                return res.json(tasks)
            }
        })
        .catch(err => {
            return res.status(400).json("Error" + err)
        })
});

//update a task
router.route('/task/:id').put((req, res) => {
    db.collection("tasks").doc(req.params.id).update(req.body)
        .then(() => {
            return res.status(201).json({ message: "update task success" })
        })
        .catch(err => {
            return res.status(400).json("Error" + err)
        })
});

//get a task
router.route('/task/:id').get((req, res) => {
    db.collection("tasks").doc(req.params.id).get()
        .then(task => {
            return res.status(201).json({ message: "get task success", task: task.data() })
        })
        .catch(err => {
            return res.status(400).json("Error" + err)
        })
});

//delete a task
router.route('/task/:id').delete((req, res) => {
    db.collection("tasks").doc(req.params.id).delete()
        .then(project => {
            return res.status(200).json({ message: "task successfully deleted", name: project.name })
        })
        .catch(err => res.status(400).json('Error: ' + err));
});


module.exports = router;
