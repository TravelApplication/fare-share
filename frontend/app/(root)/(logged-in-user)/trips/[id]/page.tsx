async function Page({ params }: { params: { id: string } }) {
  return <div>details of trip with id: {`${params.id}`}</div>;
}

export default Page;
