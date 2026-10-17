from fastapi import FastAPI
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
import numpy as np

app = FastAPI(title="SMARTS SBERT Service")

# Load SBERT model once when the service starts
model = SentenceTransformer("all-MiniLM-L6-v2")


class SimilarityRequest(BaseModel):
    text1: str
    text2: str


class SimilarityResponse(BaseModel):
    similarity: float


@app.get("/")
def home():
    return {
        "service": "SMARTS SBERT Service",
        "status": "running"
    }


@app.post("/similarity", response_model=SimilarityResponse)
def calculate_similarity(request: SimilarityRequest):

    embedding1 = model.encode(request.text1)
    embedding2 = model.encode(request.text2)

    similarity = np.dot(embedding1, embedding2) / (
        np.linalg.norm(embedding1) *
        np.linalg.norm(embedding2)
    )

    return {
        "similarity": round(float(similarity), 4)
    }