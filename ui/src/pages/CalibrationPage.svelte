<script>
    import { Page, Navbar, NavRight, Link, List, ListInput, 
              BlockTitle, Block, Button, ListItem, Row, Col } from 'framework7-svelte';

			  const urlParams = new URLSearchParams(window.location.search);
    let clientId = urlParams.get('clientId');
  


    async function saveReference(){
      console.log(await JSInterface.saveReferencePoint());
    }

    async function startClick(){
      	document.getElementById("start").className = "col button button-raised button-fill";
	  	await fetch("http://" + location.hostname + ":80/offline/start", {
			method: 'PUT',
			body: JSON.stringify({
					type: "offline",
					id: clientId
				})
		});
    }

    async function stopClick(){
      document.getElementById("start").className = "col button button-raised";
	  await fetch("http://" + location.hostname + ":80/offline/stop", {
			method: 'PUT',
			body: JSON.stringify({
					type: "offline",
					id: clientId
				})
		})
    } 

</script>

<Page>
    <!-- Top Navbar -->
    <Navbar title="Add room"  backLink="back">
		<NavRight>
			<Link iconMd="material:home" iconOnly href="/musiclist/" />
		  </NavRight>
	</Navbar>
    <!--<Fab position="center-bottom" text="Save">
        <Icon md="material:done"></Icon>
      </Fab>-->
    <List noHairlinesMd>
      <ListInput
        outline
        label="Room name"
        floatingLabel
        type="text"
        placeholder="Bed room"
      ></ListInput>
    </List>
    <BlockTitle>Calibration nuovo</BlockTitle>
    <Block strong>
      <Row tag="p">
        <Col tag="span">
          <Button id="start" class="col button button-raised" on:click={startClick}>start</Button>
        </Col>
        <Col tag="span">
          <Button col class="col button button-raised" on:click={stopClick}>stop</Button>
        </Col>
      </Row>
    </Block>
    <!--<List mediaList>
      <ListItem
        title="Yellow Submarine"
        subtitle="Beatles">
        <img slot="media" src="https://cdn.framework7.io/placeholder/fashion-88x88-1.jpg" width="44" />
      </ListItem>
      <ListItem
        link="#"
        title="Don't Stop Me Now"
        subtitle="Queen">
        <img slot="media" src="https://cdn.framework7.io/placeholder/fashion-88x88-2.jpg" width="44" />
      </ListItem>
      <ListItem
        title="Billie Jean"
        subtitle="Michael Jackson">
        <img slot="media" src="https://cdn.framework7.io/placeholder/fashion-88x88-3.jpg" width="44" />
      </ListItem>
    </List>-->
</Page>
