const { Firestore } = require("@google-cloud/firestore");
const InputError = require("../exceptions/InputError");
const ClientError = require("../exceptions/ClientError");
require("dotenv").config();

const db = new Firestore({
  keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
});


async function storeData(id, data) {
  try {
    const { email } = data;
    const isDuplicate = db.collection("users").where("email", "==", email).limit(1);
    const snapshoot = await isDuplicate.get();
    if (!snapshoot.empty) {
      throw new ClientError(`email ${email} already exist`);
    }
    const userColection = db.collection("users");
    return userColection.doc(id).set(data);
  } catch (error) {
    throw new ClientError(error);
  }
}

const addImageKey = async (id, profileUrl) => {
  const userAccount = db.collection('users').doc(id);
  try {
    const docSnapshot = await userAccount.get();
    if (!docSnapshot.exists) {
      throw new Error("User not found");
    }
    
    const data = docSnapshot.data();
    if (data && data.profileUrl) {
      await userAccount.update({ profileUrl })
      return;
    }

    await userAccount.set(
      { profileUrl },
      { merge: true }
    );

  } catch (error) {
    throw new Error(error);
  }
};

const verifyUser = async (email) => {
  const userAccount = db.collection("users").where("email", "==", email).limit(1);
  const data = await userAccount.get();
  if (data.empty) {
    throw new InputError("user not found");
  }
  const user = data.docs[0].data();

  return user;
};

const getProfile = async (nameId) => {
  const userAccount = await db.collection("users").doc(nameId).get();

  if (userAccount.empty) {
    throw new ClientError("Profile not found");
  }

  const data = userAccount.data();
  return data;
};

const updateUser = async (account, data) => {
  try {
    const userAccount = await verifyUser(account);
    const document = db.collection("users").doc(userAccount.nameId);
    await document.update(data);
  } catch (error) {
    throw new ClientError(error);
  }
};

const postProgress = async (nameId, id, data) => {
  try {
    const userDoc = db.collection("users").doc(nameId);
    const historiesCollection = userDoc.collection("progress");
    const isNameDuplicate = await historiesCollection.where("name", "==", data.name).limit(1).get()
    const isDateDuplicate = await historiesCollection.where("date", "==", data.date).limit(1).get()
    if (!isDateDuplicate.empty && !isNameDuplicate.empty) {
      throw new Error("you already have this schedule")
    }
    return historiesCollection.doc(id).set(data);
  } catch (error) {
    throw new ClientError(error);
  }
};


const getHistory = async (id) => {
  const data = db.collection("users").doc(id);
  const history = await data.collection("history").get();
  if (history.empty) {
    throw new ClientError("user does not have a history");
  }

  return history.docs.map((doc) => ({
    ...doc.data(),
  }));
};

const getProgress = async (id) => {
  const data = db.collection("users").doc(id);
  const progress = await data.collection("progress").get();
  if (progress.empty) {
    throw new ClientError("user does not have a progress");
  }

  return progress.docs.map((doc) => ({
    ...doc.data(),
  }));
};

const getDetailHistory = async (nameId, historyId) => {
  const userAccount = db.collection("users").doc(nameId);
  if (!userAccount) {
    throw new ClientError("user not fount", 404);
  }
  const history = await userAccount.collection("history").doc(historyId).get();
  if (!history.exists) {
    throw new ClientError("history not found", 404);
  }
  return history.data();
};

const getDetailProgress = async (nameId, progressId) => {
  const userAccount = db.collection("users").doc(nameId);
  if (!userAccount) {
    throw new ClientError("user not fount", 404);
  }
  const progress = await userAccount.collection("progress").doc(progressId).get();
  if (!progress.exists) {
    throw new ClientError("progress not founf", 404);
  }
  return progress.data();
};

const deleteAndPost = async (nameId, id, data) => {
  try {
    const userAccount = db.collection('users').doc(nameId);
    await userAccount.collection('progress').doc(id).delete();
    await userAccount.collection('history').doc(id).set(data);
  } catch (error) {
    throw new ClientError(error);
  }
}

module.exports = { storeData, verifyUser, getProfile, updateUser, postProgress, getHistory, addImageKey, deleteAndPost, getProgress, getDetailHistory, getDetailProgress };
