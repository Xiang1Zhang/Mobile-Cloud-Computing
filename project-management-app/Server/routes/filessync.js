const router = require('express').Router();
const {admin, db} = require('../utils/admin');


//create a new project
router.route('/files/imagesU').put((req, res) => {
    db.collection("files").doc(req.body.projectID).update({
        images: admin.firestore.FieldValue.arrayUnion(req.body.imageUrl)
    })
    .then(() => {
        return res.status(201).json({message:"update image url success"})
    })
    .catch(err =>{
        return res.status(400).json("Error"+err)
    })
});

router.route('/files/imagesD').put((req, res) => {
    db.collection("files").doc(req.body.projectID).update({
        images: admin.firestore.FieldValue.arrayRemove(req.body.imageUrl)
    })
    .then(() => {
        return res.status(201).json({message:"Remove image url success"})
    })
    .catch(err =>{
        return res.status(400).json("Error"+err)
    })
});

router.route('/files/docsU').put((req, res) => {
    db.collection("files").doc(req.body.projectID).update({
        docs: admin.firestore.FieldValue.arrayUnion(req.body.docsUrl)
    })
    .then(() => {
        return res.status(201).json({message:"update file url success"})
    })
    .catch(err =>{
        return res.status(400).json("Error"+err)
    })
});

router.route('/files/docsD').put((req, res) => {
    db.collection("files").doc(req.body.projectID).update({
        docs: admin.firestore.FieldValue.arrayRemove(req.body.docsUrl)
    })
    .then(() => {
        return res.status(201).json({message:"Remove file url success"})
    })
    .catch(err =>{
        return res.status(400).json("Error"+err)
    })
});


router.route('/files/:id').get((req, res) => {
    console.log(req.params.id)
    db.collection("files").doc(req.params.id).get()
        .then(files => {
            let fileData = files.data();
            return res.status(201).json({ message: "get images success", images: fileData.images, docs: fileData.docs })
        })
        .catch(err => {
            return res.status(400).json("Error" + err)
        })
});

router.route('/images/:id').get((req, res) => {
    console.log(req.params.id)
    db.collection("files").doc(req.params.id).get()
        .then(images => {
            return res.status(201).json({ message: "get images success", images: images.data().images })
        })
        .catch(err => {
            return res.status(400).json("Error" + err)
        })
});

router.route('/docs/:id').get((req, res) => {
    console.log(req.params.id)
    db.collection("files").doc(req.params.id).get()
        .then(images => {
            return res.status(201).json({ message: "get images success", images: images.data().docs })
        })
        .catch(err => {
            return res.status(400).json("Error" + err)
        })
});

module.exports = router;

