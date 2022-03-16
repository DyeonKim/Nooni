from flask import Flask
from flask import request
import torch
import torch.nn as nn
import numpy as np
import hparams as hp
import firebase_admin
from firebase_admin import credentials
from firebase_admin import storage
from werkzeug.utils import secure_filename
import os
from uuid import uuid4
from flask import send_file

os.environ["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID"
os.environ["CUDA_VISIBLE_DEVICES"] = hp.synth_visible_devices

import argparse
import re
from string import punctuation

from fastspeech2 import FastSpeech2
from vocoder import vocgan_generator

from text import text_to_sequence, sequence_to_text
import utils
import audio as Audio

import codecs
from g2pk import G2p
from jamo import h2j

app = Flask(__name__)

device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

PROJECT_ID = "nooni-a587a"
cred = credentials.Certificate("nooni-a587a-firebase-adminsdk-70hsn-4071f9a453.json")
default_app = firebase_admin.initialize_app(cred, {
    'storageBucket': f"{PROJECT_ID}.appspot.com"
})
bucket = storage.bucket()  # 기본 버킷 사용


def fileUpload(file):
    blob = bucket.blob(file)
    # new token and metadata 설정
    new_token = uuid4()
    metadata = {"firebaseStorageDownloadTokens": new_token}  # access token이 필요하다.
    blob.metadata = metadata

    # upload file
    blob.upload_from_filename(filename='./' + file, content_type='audio/wav')
    print(blob.public_url)

def kor_preprocess(text):
    text = text.rstrip(punctuation)

    g2p = G2p()
    phone = g2p(text)
    print('after g2p: ', phone)
    phone = h2j(phone)
    print('after h2j: ', phone)
    phone = list(filter(lambda p: p != ' ', phone))
    phone = '{' + '}{'.join(phone) + '}'
    print('phone: ', phone)
    phone = re.sub(r'\{[^\w\s]?\}', '{sp}', phone)
    print('after re.sub: ', phone)
    phone = phone.replace('}{', ' ')

    print('|' + phone + '|')
    sequence = np.array(text_to_sequence(phone, hp.text_cleaners))
    sequence = np.stack([sequence])
    return torch.from_numpy(sequence).long().to(device)


def get_FastSpeech2(num):
    checkpoint_path = os.path.join(hp.checkpoint_path, "checkpoint_{}.pth.tar".format(num))
    model = nn.DataParallel(FastSpeech2())
    model.load_state_dict(torch.load(checkpoint_path)['model'])
    model.requires_grad = False
    model.eval()
    return model


def synthesize(model, vocoder, text, sentence, prefix=''):
    sentence = sentence[:10]  # long filename will result in OS Error

    mean_mel, std_mel = torch.tensor(np.load(os.path.join(hp.preprocessed_path, "mel_stat.npy")), dtype=torch.float).to(
        device)
    mean_f0, std_f0 = torch.tensor(np.load(os.path.join(hp.preprocessed_path, "f0_stat.npy")), dtype=torch.float).to(
        device)
    mean_energy, std_energy = torch.tensor(np.load(os.path.join(hp.preprocessed_path, "energy_stat.npy")),
                                           dtype=torch.float).to(device)

    mean_mel, std_mel = mean_mel.reshape(1, -1), std_mel.reshape(1, -1)
    mean_f0, std_f0 = mean_f0.reshape(1, -1), std_f0.reshape(1, -1)
    mean_energy, std_energy = mean_energy.reshape(1, -1), std_energy.reshape(1, -1)

    src_len = torch.from_numpy(np.array([text.shape[1]])).to(device)

    mel, mel_postnet, log_duration_output, f0_output, energy_output, _, _, mel_len = model(text, src_len)

    mel_torch = mel.transpose(1, 2).detach()
    mel_postnet_torch = mel_postnet.transpose(1, 2).detach()
    f0_output = f0_output[0]
    energy_output = energy_output[0]

    mel_torch = utils.de_norm(mel_torch.transpose(1, 2), mean_mel, std_mel)
    mel_postnet_torch = utils.de_norm(mel_postnet_torch.transpose(1, 2), mean_mel, std_mel).transpose(1, 2)
    f0_output = utils.de_norm(f0_output, mean_f0, std_f0).squeeze().detach().cpu().numpy()
    energy_output = utils.de_norm(energy_output, mean_energy, std_energy).squeeze().detach().cpu().numpy()

    if not os.path.exists(hp.test_path):
        os.makedirs(hp.test_path)

    Audio.tools.inv_mel_spec(mel_postnet_torch[0],
                             os.path.join(hp.test_path, 'griffin_lim_{}.wav'.format( sentence)))

    if vocoder is not None:
        if hp.vocoder.lower() == "vocgan":
            utils.vocgan_infer(mel_postnet_torch, vocoder,
                               path=os.path.join(hp.test_path, '{}_{}.wav'.format(hp.vocoder, sentence)))

    utils.plot_data([(mel_postnet_torch[0].detach().cpu().numpy(), f0_output, energy_output)],
                    ['Synthesized Spectrogram'],
                    filename=os.path.join(hp.test_path, '{}_{}.png'.format(prefix, sentence)))



@app.route('/test',methods=['GET'])
def index():
    
    args = 350000 
    model = get_FastSpeech2(350000).to(device)
    if hp.vocoder == 'vocgan':
        vocoder = utils.get_vocgan(ckpt_path=hp.vocoder_pretrained_model_path)
    else:
        vocoder = None


    test_sentence = request.args.get('msg')

    g2p = G2p()
    sentence = test_sentence
    print(sentence)
    text = kor_preprocess(sentence)
    synthesize(model, vocoder, text, sentence, prefix='')
    path_to_file = os.path.join(hp.test_path,'{}_{}.wav'.format(hp.vocoder,sentence))
    fileUpload("results/"+'{}_{}.wav'.format(hp.vocoder,sentence))
    return "https://storage.googleapis.com/nooni-a587a.appspot.com/results/"+'{}_{}.wav'.format(hp.vocoder,sentence)


