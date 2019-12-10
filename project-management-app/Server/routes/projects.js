const router = require('express').Router();
const { db, admin } = require('../utils/admin');

let randomNumber = (min, max) => Math.floor(Math.random() * (max - min)) + min;
let randomBoolean = () => Math.random() >= 0.5;
let randomDate = (from, to) => {
    from = from.getTime();
    to = to.getTime();
    return new Date(from + Math.random() * (to - from));
};

let sortByDateProperty = (project1, project2, property, isAscending) => {

    let project1Before = isAscending ? 1 : -1;
    let project2Before = isAscending ? -1 : 1;

    if (!project1.hasOwnProperty(property))
        return !project2.hasOwnProperty(property) ? 0 : -1;
    else if (!project2.hasOwnProperty(property))
        return 1;
    else {
        let date1 = new Date(project1[property]);
        let date2 = new Date(project2[property]);
        return (date1 === date2) ? 0 : ((date1 > date2) ? project1Before : project2Before)
    }
    //TODO fix sorting
};


router.route("/fix").get((req, res) => {

    db.collection('projects').get().then(resp => {
        let batch = db.batch();

        resp.docs.forEach((queryDocumentSnapshot => {
            let project = queryDocumentSnapshot.data();
            if (randomBoolean()) {
                project.modificationDate = new Date().toISOString();
                batch.update(queryDocumentSnapshot.ref, project);
            }
        }));

        batch.commit().then(documentReference => {
            return res.status(201).json({ message: "Update projects success" })
        })
            .catch(err => {
                return res.status(400).json({ err: err.message, message: err.message })
            });

    })

});


router.route("/generateProjU").get((req, res) => {

    db.collection('projects').get().then(resp => {
        let projectsU = [];
        let projects = resp.docs.map(queryDocumentSnapshot => {
            return ({
                id: queryDocumentSnapshot.id,
                modificationDate: new Date().toISOString()
            })
        });

        projects.forEach(item => {
            projectsU.push(item);
        });

        let batch = db.batch();

        projectsU.forEach((projectU) => {
            let ref = db.collection("projects").doc(projectU.id);
            batch.update(ref, projectU)
        });

        batch.commit().then(documentReference => {
            return res.status(201).json({ message: "update project success" })
        })
            .catch(err => {
                return res.status(400).json({ err: err, message: err.message })
            });

    })
        .catch(err => {
            return res.status(400).json({ err: err.message })
        })
});

//create a new project
router.route('/project').post((req, res) => {

    console.log(req.rawBody);

    let uid = req.uid;
    const creationDate = new Date();

    let project = {
        name: req.body.name,
        description: req.body.description,
        isPersonal: req.body.isPersonal,
        administrator: uid,
        creationDate: creationDate.toISOString(),
        isFavorite: req.body.isFavorite,
        modificationDate: creationDate.toISOString()
    };

    for(let el in project){
        if(project[el] === undefined){
            return res.status(400).json({err: "Missing field " + el, message: "Error creating the project"})
        }
    }

    if(req.body.keywords){
        project.keywords = req.body.keywords
    }

    if(req.body.deadline){
        project.deadline = req.body.deadline
    }

    if (!project.isPersonal) {
        project.collaborators = req.body.collaborators;
        project.isFavorite = req.body.isFavorite;
    }

    console.log(project);

    db.collection("projects").add(project).then(projectRecord => {


        db.collection("files").doc(projectRecord.id).set({
            images: null,
            docs: null
        }).then(() => {

            projectRecord.get().then(el => {
                let projectRet = el.data();
                projectRet.id = el.id;
                return res.status(201).json({ message: "create project success", data: projectRet })
            });

        })
        .catch(err => {
            return res.status(400).json({ err: err.message, message: "Error creating the project" })
        })

    }).catch(err => {
        return res.status(400).json({ err: err.message, message: "Error creating the project" })
    })

});

//get a project info
router.route('/project/:id').get((req, res) => {
    db.collection('projects').doc(req.params.id).get()
        .then(project => {
            const projectData = project.data();

            if (!projectData)
                return res.status(404).json({ message: "No project with this id" });
            else
                return res.status(200).json({ message: "get project success", data: projectData })

        })
        .catch(err => res.status(400).json({ err: err.message, message: "Error getting the project" }));
});

//delete a project
router.route('/project/:id').delete((req, res) => {

    console.log("DOING THE DELETING");

    let userId = req.uid;
    let projectId = req.params.id;

    db.collection("projects").doc(projectId).get().then(project => {

        const projectData = project.data();

        if (!projectData)
            return res.status(404).json({ message: "No project with this id" });

        if (projectData.administrator !== userId)
            return res.status(403).end();

        db.collection("projects").doc(projectId).delete().then(() => {

            console.log("deleting did actually happen - project");

            db.collection("tasks").where('projectID', '==', projectId).get().then(resp => {

                let batch = db.batch();
                resp.docs.forEach(queryDocumentSnapshot => batch.delete(queryDocumentSnapshot.ref));

                batch.commit()
                    .then(() => {
                        console.log("deleting did actually happen - tasks");
                        return res.status(200).json({ message: "Project successfully deleted " + project.name })
                    })
                    .catch(err => res.status(400).json({ err: err.message, message: "Error deleting the tasks of the project" }));

            })
                .catch(err => res.status(400).json({ err: err.message, message: "Error getting the tasks of the project" }));

        })
            .catch(err => res.status(400).json({ err: err.message, message: "Error deleting the project" }));

    });


});

//update a project
router.route('/project/:id').put((req, res) => {
    let docRef = db.collection('projects').doc(req.params.id);
    docRef.update(req.body)
        .then(() => {
            let modificationDate = new Date();
            docRef.update({ modificationDate: modificationDate.toISOString() })
                .then(project => {
                    return res.status(201).json({ message: "update project success" })
                })
                .catch(err => res.status(400).json({ err: err.message, message: "Error updating the project modification date" }));
        })
        .catch(err => res.status(400).json({ err: err.message, message: "Error updating the project" }));
});


//update isFavorite
router.route('/project/:id/isFavorite').put((req, res) => {

    let uid = req.uid;
    let modificationDate = new Date();
    let projectId = req.params.id;

    let docRef = db.collection('projects').doc(projectId);

    docRef.get().then(projectRef => {

        let isFavorite = projectRef.data().isFavorite;
        isFavorite[uid] = req.body.isFavorite;

        return docRef.update({
            isFavorite: isFavorite,
            modificationDate: modificationDate.toISOString()
        });

    })
        .then(() => {
            return res.status(201).json({ message: "update isFavorite success" })
        })
        .catch(err => {
            return res.status(400).json({ err: err.message, message: "Could not update isFavorite" })
        })

});

//Get this user's projects
router.route('/projects/:kind').get((req, res) => {

    let projectsArray = [];
    let kind = req.params.kind;

    db.collection('projects').where("collaborators", 'array-contains', req.uid).get().then(resp => {

        if (!resp.empty) {
            let others = resp.docs.map((queryDocumentSnapshot => {
                let project = queryDocumentSnapshot.data();
                project.id = queryDocumentSnapshot.id;
                return project;
            }));

            projectsArray = projectsArray.concat(others);
        }

        //here to collect admin projects of current user
        db.collection('projects').where("administrator", "==", req.uid).get()
            .then(resp => {
                if (!resp.empty) {
                    let mine = resp.docs.map((queryDocumentSnapshot => {
                        let project = queryDocumentSnapshot.data();
                        project.id = queryDocumentSnapshot.id;
                        return project;
                    }));
                    projectsArray = projectsArray.concat(mine);
                }

                if (kind === "deadline") {
                    projectsArray = projectsArray.sort((project1, project2) =>
                        sortByDateProperty(project1, project2, kind, true))
                }
                else if (kind === "modificationDate") {
                    projectsArray = projectsArray.sort((project1, project2) =>
                        sortByDateProperty(project1, project2, kind, false))
                }
                else if (kind === "favorite") {
                    projectsArray = projectsArray.filter(project => project.isFavorite[req.uid]);
                }

                console.log("Returning " + projectsArray.length + " projects");
                console.log({ data: projectsArray });
                return res.json({ data: projectsArray, message: "Successfully got projects" })

            })
            .catch(err => {
                return res.status(400).json({ err: err.message, message: "Error getting projects where user in admin" })
            })

    })
    .catch(err => {
        return res.status(400).json({ err: err.message, message: "Error getting projects where user in collaborator" })
    });

});


//Search Projects
router.route('/projects/:by/search').get((req, res) => {

    let by = req.params.by;
    let query = req.query.q;

    switch (by) {
        case 'name' :
        case 'keywords':
            db.collection('projects').where(by, ">=", query).get().then(resp => {
                if (resp.empty) {
                    return res.json({ message: "No matching projects.", data: []})
                }
                else {
                    let data = resp.docs.map(( queryDocumentSnapshot => {
                        let project = queryDocumentSnapshot.data();
                        project.id = queryDocumentSnapshot.id;
                        return project;
                    }));

                    return res.json({data, message: "Successful project search"});
                }

            })
            .catch(err => {
                return res.status(400).json({err: err.message, message: "Error searching projects"})
            });
            break;

        default:
            return res.status(403).end();
    }

});


module.exports = router;
