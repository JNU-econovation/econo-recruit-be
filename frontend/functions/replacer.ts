const cellPhoneNumberReplacer = (value: string) =>
  value
    .replace(/[^0-9]/g, "")
    .replace(/^(\d{2,3})(\d{3,4})(\d{4})$/, `$1-$2-$3`);

const undergradeNumberReplacer = (value: string) =>
  value.replace(/[^0-9]/g, "");

export type ReplacerType = "cellPhoneNumber" | "undergradeNumber";

export const replacer = (value: string, type: ReplacerType) => {
  switch (type) {
    case "cellPhoneNumber":
      return cellPhoneNumberReplacer(value);
    case "undergradeNumber":
      return undergradeNumberReplacer(value);
    default:
      return value;
  }
};
