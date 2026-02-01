const SentimentAnalysisSchema = {
  analysisId: { type: String, required: true, unique: true },
  userId: { type: String, required: true },
  sessionId: { type: String },
  originalText: { type: String, required: true },
  sentiment: { 
    type: String, 
    enum: ['positive', 'negative', 'neutral', 'mixed'],
    required: true 
  },
  score: { type: Number, required: true }, // -1 a 1
  confidence: { type: Number }, // 0 a 1
  analysisDetails: {
    positiveWords: [String],
    negativeWords: [String],
    neutralWords: [String],
    language: String,
    textLength: Number
  },
  metadata: {
    processingTime: Number,
    algorithmVersion: String,
    timestamp: { type: Date, default: Date.now }
  }
};