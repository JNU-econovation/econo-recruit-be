import { https } from "@/src/functions/axios";

export interface ApplicantReq {
  type: string;
  name: string;
  answer: string;
}

export const getApplicant = async (id: string) => {
  const { data } = await https.get<ApplicantReq[]>(`/applicants/${id}`);

  return data;
};

export interface ApplicantLabelReq {
  name: string;
  active: boolean;
}

export const getApplicantLabel = async (id: string) => {
  const { data } = await https.get<ApplicantLabelReq[]>(
    `/applicants/${id}/label`
  );

  return data;
};

export interface ApplicantCommentReq {
  interviewerName: string;
  content: string;
  isLike: boolean;
  likeCount: number;
  createAt: number;
  canEdit: boolean;
}

export const getApplicantComment = async (id: string) => {
  const { data } = await https.get<ApplicantCommentReq[]>(`/comments/${id}`);

  return data;
};