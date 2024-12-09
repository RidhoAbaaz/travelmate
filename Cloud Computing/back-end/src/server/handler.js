const { nanoid } = require("nanoid");
const InputError = require("../exceptions/InputError");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const { storeData, verifyUser, getProfile, updateUser, getHistory, addImageKey, deleteAndPost, postProgress, getProgress, getDetailProgress, getDetailHistory } = require("../services/firestore");
const uploadIImageToGCS = require("../services/storage");
const axios = require('axios');
const ClientError = require("../exceptions/ClientError");
require("dotenv").config();

const postImageProfileHandler = async (request, h) => {
  const { nameId } = request.user;
  const { image } = request.payload;
  if (!image) {
    throw new InputError('parameter image harus diisi');
  }
  const buffer = Buffer.from(image, "base64");

  const profileUrl = await uploadIImageToGCS(buffer, `${nameId}.jpg`);

  await addImageKey(nameId, profileUrl);

  return h.response({
    status: "success",
    message: "image upload successfully",
    imageUrl: profileUrl,
  }).code(201);
}

const postUserHandler = async (request, h) => {
  const { name, email, password } = request.payload;
  const nameId = nanoid(16);
  const createAt = new Date().toISOString();
  const updateAt = createAt;

  if (!name) {
    throw new InputError("Maaf, parameter nama harus diisi.");
  }
  if (!email) {
    throw new InputError("Maaf, parameter email harus diisi.");
  }
  if (!password) {
    throw new InputError("Maaf, parameter password harus diisi.");
  }

  const hashedPassword = await bcrypt.hash(password, 10);

  const newUser = {
    nameId,
    name,
    email,
    hashedPassword,
    createAt,
    updateAt,
  };

  await storeData(nameId, newUser);

  const token = jwt.sign({ nameId }, process.env.JWT_SECRET, { expiresIn: "12h" });

  return h.response({
      status: "success",
      message: "User added successfully",
      id: nameId,
      token: token,
    })
    .code(201);
};

const getUserByEmailHandler = async (request, h) => {
  const { email, password } = request.payload;

  if (!email) {
    throw new InputError("parameter email tidak boleh kosong");
  }

  if (!password) {
    throw new InputError("parameter password tidak boleh kosong");
  }

  const userAccount = await verifyUser(email);

  const hasPassword = userAccount.hashedPassword;

  const isMatch = await bcrypt.compare(password, hasPassword);
  if (isMatch) {
    const token = jwt.sign({ nameId: userAccount.nameId }, process.env.JWT_SECRET, { expiresIn: "1h" });
    return h
      .response({
        status: "success",
        message: "loggin success",
        token: token,
      })
      .code(200);
  }

  throw new InputError("password incorect");
};

const getProfileHandler = async (request, h) => {
  const { nameId } = request.user;
  const data = await getProfile(nameId);

  return h.response({
    status: "success",
    data: data,
  });
};

const updateUserHandler = async (request, h) => {
  const { name, password, confirmPassword } = request.payload;

  if (!name) {
    throw new InputError("nama tidak boleh kosong");
  }

  if (!password) {
    throw new InputError("password tidak boeleh kosong");
  }

  if (!confirmPassword) {
    throw new InputError("verifikasi password tidak boeleh kosong");
  }

  if (password !== confirmPassword) {
    throw new InputError(`${password} and ${confirmPassword} not match`);
  }

  const updateAt = new Date().toISOString();

  const hashedPassword = await bcrypt.hash(password, 10);
  const newPw = {
    hashedPassword: hashedPassword,
    updateAt: updateAt,
  };
  await updateUser(name, newPw);

  return h.response({
    status: "success",
    message: "password successfully update",
  });
};

const postActivityHandler = async (request, h) => {
  try {
    const { nameId } = request.user;
    const { placeId } = request.params;
    const { date } = request.payload;
    const response = await axios.get(`https://travelmate-model-372279187520.asia-southeast2.run.app/home/recomendation/${placeId}`);
    const data = response.data;
    const id  = nanoid(16);
    const insertedAt = new Date().toISOString();
    const updateAt = insertedAt;
  
    const addActivity = {
      id: id,
      name: data.name,
      city: data.city,
      price: data.price,
      rating: data.rating,
      category: data.category,
      address: data.address,
      date: date,
      insertedAt: insertedAt, 
      updateAt: updateAt
    }
  
    await postProgress(nameId, id, addActivity);
  
    return h.response({
        status: 'success',
        message: 'adtivity added succesfully',
        id: id
    }).code(201);
  } catch (error) {
    throw new ClientError(error);
  }
}

const getProgressHandler = async (request, h) => {
  const { nameId } = request.user;

  const progress = await getProgress(nameId);

  if (!progress) {
    throw new InputError(`${idUser} tidak memiliki progress`);
  }

  return h.response({
    status: "success",
    progress: progress,
  });
};

const getHistoryHandler = async (request, h) => {
  const { nameId } = request.user;

  const history = await getHistory(nameId);

  if (!history) {
    throw new InputError(`${idUser} tidak memiliki history`);
  }

  return h.response({
    status: "success",
    history: history,
  });
};

const getHistoryByIdHandler = async (request, h) => {
  const { nameId } = request.user;
  const { id } = request.params;

  const detailHistory = await getDetailHistory(nameId, id);

  return h.response({
    status: "success",
    history: detailHistory,
  });
};

const getProgressByIdHandler = async (request, h) => {
  const { nameId } = request.user;
  const { id } = request.params;

  const detailProgress = await getDetailProgress(nameId, id);

  return h.response({
    status: "success",
    progress: detailProgress,
  });
}

const deleteAndPostHandler = async (request, h) => {
  const { nameId } = request.user;
  const { progressId } = request.params;

  const data = await getDetailProgress(nameId, progressId);

  await deleteAndPost(nameId, progressId, data)

  return h.response({
    status: "success",
    message: "data deleted successfully"
  }).code(200);
}

const displayPlaceHandler = async (request, h) => {
  try {
    const response = await axios.get('https://travelmate-model-372279187520.asia-southeast2.run.app/home');
    const data = response.data;
    return h.response(data).code(200);
  } catch (error) {
    throw new ClientError('cant connect to flask');
  }
}

const getRecomendationHandler = async (request, h) => {
  try {
    const payload = request.payload;
    const response = await axios.post('https://travelmate-model-372279187520.asia-southeast2.run.app/home/recomendation', payload);
    const data = response.data;
    return h.response(data).code(200);
  } catch (error) {
    throw new ClientError('cant connect to flask');
  }
}

const getRecomendationByIdHandler = async (request, h) => {
  try {
    const { id } = request.params;
    const response = await axios.get(`https://travelmate-model-372279187520.asia-southeast2.run.app/home/recomendation/${id}`);
    const data = response.data;
    const weather = await axios.get(`https://api.openweathermap.org/data/2.5/weather?q=${data.city}&appid=${process.env.API_KEY}`)
    const weatherResponse = weather.data;
    return h.response({
      status: data.status,
      id: data.id,
      name: data.name,
      description: data.description,
      city: data.city,
      address: data.address,
      price: data.price,
      rating: data.rating,
      category: data.category,
      weatherResponse
    }).code(200);
  } catch (error) {
    throw new ClientError('cant connect to flask');
  }
}

const getHomeByIdHandler = async (request, h) => {
  try {
    const { id } = request.params;
    const response = await axios.get(`https://travelmate-model-372279187520.asia-southeast2.run.app/home/${id}`);
    const data = response.data;
    const weather = await axios.get(`https://api.openweathermap.org/data/2.5/weather?q=${data.city}&appid=${process.env.API_KEY}`)
    const weatherResponse = weather.data;
    return h.response({
      status: data.status, 
      id: data.id,
      name: data.name,
      description: data.description,
      city: data.city,
      address: data.address,
      price: data.price,
      rating: data.rating,
      category: data.category,
      weatherResponse
    }).code(200);
  } catch (error) {
    throw new ClientError(error);
  }
}

module.exports = { postUserHandler, getUserByEmailHandler, getProfileHandler, updateUserHandler, postActivityHandler, getHistoryHandler, getHistoryByIdHandler, postImageProfileHandler, deleteAndPostHandler, getProgressHandler, getProgressByIdHandler, displayPlaceHandler, getRecomendationByIdHandler, getRecomendationHandler, getHomeByIdHandler };
